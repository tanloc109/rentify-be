package com.vaccinex.service;

import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.base.exception.EntityNotFoundException;
import com.vaccinex.dao.BatchDao;
import com.vaccinex.dao.VaccineDao;
import com.vaccinex.dao.VaccineScheduleDao;
import com.vaccinex.dto.response.AppointmentVerificationResponse;
import com.vaccinex.dto.response.BatchAvailabilityContext;
import com.vaccinex.pojo.Batch;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineSchedule;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import jakarta.ejb.Stateless;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Stateless
@RequiredArgsConstructor
public class AppointmentVerificationServiceImpl implements AppointmentVerificationService {

    private final VaccineDao vaccineRepository;
    private final VaccineScheduleDao vaccineScheduleRepository;
    private final BatchDao batchRepository;

    // Configurable constants
    private static final int MIN_DAYS_FOR_RESTOCK = 7;
    private static final int MAX_ADVANCE_BOOKING_DAYS = 90000;

    @Override
    public AppointmentVerificationResponse verifyAppointmentAvailability(
            Integer vaccineId,
            LocalDateTime appointmentDate
    ) {
        // Validate input parameters
        validateAppointmentRequest(vaccineId, appointmentDate);

        // Find the vaccine
        Vaccine vaccine = findVaccineWithValidation(vaccineId);

        // Calculate availability based on batch quantities
        BatchAvailabilityContext availabilityContext = calculateBatchAvailability(
                vaccine,
                appointmentDate
        );

        // Build and return verification response
        return buildAppointmentVerificationResponse(
                vaccine,
                appointmentDate,
                availabilityContext
        );
    }

    private void validateAppointmentRequest(Integer vaccineId, LocalDateTime appointmentDate) {
        LocalDateTime now = LocalDateTime.now();

        // Basic input validation
        if (vaccineId == null) {
            throw new BadRequestException("Vaccine ID cannot be empty");
        }

        // Prevent booking in the past
        if (appointmentDate.isBefore(now)) {
            throw new BadRequestException("Cannot book an appointment in the past");
        }

        // Prevent booking too far in advance
        if (appointmentDate.isAfter(now.plusDays(MAX_ADVANCE_BOOKING_DAYS))) {
throw new BadRequestException(
        "Cannot book an appointment more than " +
                MAX_ADVANCE_BOOKING_DAYS + " days in advance"
);
        }
    }

    private Vaccine findVaccineWithValidation(Integer vaccineId) {
        return vaccineRepository.findById(vaccineId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Vaccine with ID: " + vaccineId + " not found"
                ));
    }

    private BatchAvailabilityContext calculateBatchAvailability(
            Vaccine vaccine,
            LocalDateTime appointmentDate
    ) {
        LocalDate currentDate = LocalDate.now();
        LocalDate scheduledDate = appointmentDate.toLocalDate();

        // Trường hợp 1: Đặt lịch trên 14 ngày → available = true (luôn luôn chấp nhận)
        if (scheduledDate.isAfter(currentDate.plusDays(14))) {
            return BatchAvailabilityContext.builder()
                    .totalAvailable(Integer.MAX_VALUE)
                    .requiredVaccines(1)
                    .requiredVaccinesNext7Days(0)
                    .isAvailable(true)
                    .canBeRescheduled(true)
                    .availableBatches(Collections.emptyList())
                    .build();
        }

        // Find non-expired batches for the specific vaccine
        List<Batch> availableBatches = batchRepository.findByVaccineIdAndExpirationAfter(
                vaccine.getId(),
                appointmentDate
        );

        // Calculate total available quantity from batches
        int totalAvailable = availableBatches.stream()
                .mapToInt(Batch::getQuantity)
                .sum();

        // Lấy tất cả lịch hẹn PLANNED cho vaccine này
        List<VaccineSchedule> allPlannedVaccineSchedules = vaccineScheduleRepository.findAll().stream()
                .filter(schedule ->
                        vaccine.getId().equals(schedule.getVaccine().getId()) &&
                                VaccineScheduleStatus.PLANNED.equals(schedule.getStatus()) &&
                                !schedule.isDeleted()
                )
                .toList();

        // Số lịch hẹn đã đặt + 1 (cho lịch hẹn mới)
        int requiredVaccinesTotal = allPlannedVaccineSchedules.size() + 1;

        // Kiểm tra có đủ vaccine không
        boolean isAvailable = totalAvailable >= requiredVaccinesTotal;

        // Nếu không đủ vaccine, kiểm tra có thể dời lịch không
        boolean canBeRescheduled = false;
        int requiredVaccinesNext7Days = 0;

        if (!isAvailable) {
            // Đếm số lịch hẹn trong 7 ngày tới
            LocalDate nextWeekDate = currentDate.plusDays(MIN_DAYS_FOR_RESTOCK);

            List<VaccineSchedule> schedulesNext7Days = allPlannedVaccineSchedules.stream()
                    .filter(schedule -> {
                        LocalDate scheduleDate = schedule.getDate().toLocalDate();
                        return !scheduleDate.isAfter(nextWeekDate) && !scheduleDate.isBefore(currentDate);
                    })
                    .toList();

            // Số lượng vaccine cần trong 7 ngày tới
            requiredVaccinesNext7Days = schedulesNext7Days.size();

            // Thêm 1 nếu lịch hẹn này nằm trong 7 ngày tới
            if (!scheduledDate.isAfter(nextWeekDate)) {
                requiredVaccinesNext7Days++;
            }

            // Kiểm tra xem số lượng vaccine có thể đáp ứng cho 7 ngày tới không
            // Nếu totalAvailable >= requiredVaccinesNext7Days + 1 thì có thể dời lịch
            canBeRescheduled = totalAvailable >= requiredVaccinesNext7Days + 1;
        }

        return BatchAvailabilityContext.builder()
                .totalAvailable(totalAvailable)
                .requiredVaccines(requiredVaccinesTotal)
                .requiredVaccinesNext7Days(requiredVaccinesNext7Days)
                .isAvailable(isAvailable)
                .canBeRescheduled(canBeRescheduled)
                .availableBatches(availableBatches)
                .build();
    }

    private AppointmentVerificationResponse buildAppointmentVerificationResponse(
            Vaccine vaccine,
            LocalDateTime appointmentDate,
            BatchAvailabilityContext context
    ) {
        return AppointmentVerificationResponse.builder()
                .vaccineId(vaccine.getId())
                .vaccineName(vaccine.getName())
                .appointmentDate(appointmentDate)
                .isAvailable(context.isAvailable())
                .canBeRescheduled(context.canBeRescheduled())
                .availableQuantity(context.getTotalAvailable())
                .requiredQuantity(context.getRequiredVaccines())
                .requiredQuantityNext7Days(context.getRequiredVaccinesNext7Days())
                .message(generateAppointmentMessage(context))
                .build();
    }

   private String generateAppointmentMessage(BatchAvailabilityContext context) {
        if (context.isAvailable()) {
            return "Appointment can be scheduled.";
        }

        if (context.canBeRescheduled()) {
            return "Not enough vaccines to meet all appointments. The appointment can be rescheduled within the next 7 days.";
        }

        return "Not enough vaccines for appointments in the next 7 days. Please choose another date.";
    }
}