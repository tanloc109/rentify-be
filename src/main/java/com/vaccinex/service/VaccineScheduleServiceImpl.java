package com.vaccinex.service;

import com.sba301.vaccinex.dto.draft.VaccineScheduleDTO;
import com.sba301.vaccinex.dto.internal.PagingRequest;
import com.sba301.vaccinex.dto.internal.PagingResponse;
import com.sba301.vaccinex.dto.draft.VaccineDraftRequest;
import com.sba301.vaccinex.dto.response.*;
import com.sba301.vaccinex.exception.BadRequestException;
import com.sba301.vaccinex.exception.EntityNotFoundException;
import com.sba301.vaccinex.pojo.*;
import com.sba301.vaccinex.pojo.composite.VaccineIntervalId;
import com.sba301.vaccinex.pojo.enums.OrderStatus;
import com.sba301.vaccinex.pojo.enums.ServiceType;
import com.sba301.vaccinex.pojo.enums.VaccineScheduleStatus;
import com.sba301.vaccinex.repository.*;
import com.sba301.vaccinex.service.spec.AppointmentVerificationService;
import com.sba301.vaccinex.service.spec.VaccineScheduleService;
import com.sba301.vaccinex.service.spec.VaccineTimingService;
import com.sba301.vaccinex.utils.PaginationUtil;
import jakarta.ejb.Stateless;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Stateless
@RequiredArgsConstructor
public class VaccineScheduleServiceImpl implements VaccineScheduleService {

    private final VaccineScheduleRepository vaccineScheduleRepository;
    private final TransactionRepository transactionRepository;

    private final VaccineTimingService vaccineTimingService;
    private final OrderRepository orderRepository;
    private final VaccineRepository vaccineRepository;
    private final ComboRepository comboRepository;
    private final UserRepository userRepository;
    private final BatchTransactionRepository batchTransactionRepository;
    private final VaccineIntervalRepository vaccineIntervalRepository;
    private final ChildrenRepository childrenRepository;
    private final AppointmentVerificationService appointmentVerificationService;

    @Value("${business.interval-after-active-vaccine}")
    private int intervalAfterActiveVaccine;

    @Value("${business.interval-after-inactive-vaccine}")
    private int intervalAfterInactiveVaccine;

    @Override
    @Transactional
    public void deleteDraftSchedules(Integer childId) {
        vaccineScheduleRepository.deleteByStatusAndChildId(VaccineScheduleStatus.DRAFT, childId);
    }

    @Override
    @Transactional
    public void updateSchedule(Integer vaccineScheduleId, LocalDateTime newDate) {
        VaccineSchedule firstSchedule = vaccineScheduleRepository.findById(vaccineScheduleId).orElseThrow(
                () -> new RuntimeException("Lỗi hệ thống: Không tìm thấy lịch id " + vaccineScheduleId)
        );
        if (newDate.toLocalTime().isBefore(LocalTime.of(8, 0)) || newDate.toLocalTime().isAfter(LocalTime.of(20, 0))) {
            throw new BadRequestException("Thời gian vượt ngoài phạm vi làm việc");
        }
        if (firstSchedule.getStatus() != VaccineScheduleStatus.DRAFT) {
            throw new BadRequestException("Lỗi: Lịch đã không còn ở trạng thái nháp");
        }
        if (firstSchedule.getDate().isAfter(newDate)) {
            throw new BadRequestException("Xin lỗi, bạn không thể điều chỉnh lịch về quá khứ, chỉ có thể dời vào tương lai.");
        }
        if (vaccineScheduleRepository.existsByDoctorAndDate(firstSchedule.getDoctor(), newDate)) {
            throw new BadRequestException("Lỗi: Bác sĩ " + firstSchedule.getDoctor().getFullName() + " đã có lịch vào giờ đấy, xin hãy chọn giờ khác");
        }

        // Calculate the time difference
        Duration duration = Duration.between(firstSchedule.getDate(), newDate);

        // Update the first schedule's date
        firstSchedule.setDate(newDate);
        vaccineScheduleRepository.save(firstSchedule);

        // Retrieve all future schedules
        Child child = firstSchedule.getChild();
        List<VaccineSchedule> schedules = vaccineScheduleRepository.findByChildIdAndDateAfterOrderByDateAsc(child.getId(), firstSchedule.getDate());
        for (VaccineSchedule schedule : schedules) {
            LocalDateTime newScheduleDate = schedule.getDate().plus(duration);
            if (newScheduleDate.toLocalTime().isBefore(LocalTime.of(8, 0))) {

                // Move to the next working day at 8 AM
                LocalDateTime updatedSchedule = newScheduleDate.withHour(8).withMinute(0);

                // Update the required duration
                duration = Duration.between(updatedSchedule, schedule.getDate());

                newScheduleDate = updatedSchedule;
            } else if (newScheduleDate.toLocalTime().isAfter(LocalTime.of(20, 0))) {

                // Move to the next day at 8 AM
                LocalDateTime updatedSchedule = newScheduleDate.plusDays(1).withHour(8).withMinute(0);

                // Update the required duration
                duration = Duration.between(updatedSchedule, schedule.getDate());

                // Update the required duration
                newScheduleDate = updatedSchedule;
            }
            schedule.setDate(newScheduleDate);
            vaccineScheduleRepository.save(schedule);
        }
    }

    @Override
    @Transactional
    public void updateExistingSchedule(Integer vaccineScheduleId, LocalDateTime newDate) {
        VaccineSchedule firstSchedule = vaccineScheduleRepository.findById(vaccineScheduleId).orElseThrow(
                () -> new RuntimeException("Lỗi hệ thống: Không tìm thấy lịch id " + vaccineScheduleId)
        );
        if (newDate.toLocalTime().isBefore(LocalTime.of(8, 0)) || newDate.toLocalTime().isAfter(LocalTime.of(20, 0))) {
            throw new BadRequestException("Thời gian nằm ngoài phạm vi làm việc");
        }
        if (firstSchedule.getDate().isAfter(newDate)) {
            throw new BadRequestException("Xin lỗi, bạn không thể điều chỉnh lịch về quá khứ, chỉ có thể dời vào tương lai.");
        }
        if (Duration.between(firstSchedule.getDate(), newDate).toDays() > 6) {
            throw new BadRequestException("Bạn không thể dời lịch quá 6 ngày.");
        }
        if (vaccineScheduleRepository.existsByDoctorAndDate(firstSchedule.getDoctor(), newDate)) {
            throw new BadRequestException("Lỗi: Bác sĩ " + firstSchedule.getDoctor().getFullName() + " đã có lịch vào giờ đấy, xin hãy chọn giờ khác");
        }

        Duration duration = Duration.between(firstSchedule.getDate(), newDate);

        firstSchedule.setDate(newDate);
        vaccineScheduleRepository.save(firstSchedule);

        Child child = firstSchedule.getChild();
        List<VaccineSchedule> schedules = vaccineScheduleRepository.findByChildIdAndDateAfterOrderByDateAsc(child.getId(), firstSchedule.getDate());
        for (VaccineSchedule schedule : schedules) {
            if (schedule.getStatus() == VaccineScheduleStatus.PLANNED) {
                LocalDateTime newScheduleDate = schedule.getDate().plus(duration);
                if (newScheduleDate.toLocalTime().isBefore(LocalTime.of(8, 0))) {

                    // Move to the next working day at 8 AM
                    LocalDateTime updatedSchedule = newScheduleDate.withHour(8).withMinute(0);

                    // Update the required duration
                    duration = Duration.between(updatedSchedule, schedule.getDate());

                    newScheduleDate = updatedSchedule;
                } else if (newScheduleDate.toLocalTime().isAfter(LocalTime.of(20, 0))) {

                    // Move to the next day at 8 AM
                    LocalDateTime updatedSchedule = newScheduleDate.plusDays(1).withHour(8).withMinute(0);

                    // Update the required duration
                    duration = Duration.between(updatedSchedule, schedule.getDate());

                    // Update the required duration
                    newScheduleDate = updatedSchedule;
                }
                schedule.setDate(newScheduleDate);
                vaccineScheduleRepository.save(schedule);
            }
        }

    }

    @Override
    public List<DoctorScheduleResponse> getDoctorSchedule(Integer doctorId, LocalDate date) {
        return vaccineScheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getDoctor().getId().equals(doctorId))
                .filter(schedule -> date == null ? schedule.getDate().toLocalDate().equals(LocalDate.now()) : schedule.getDate().toLocalDate().equals(date))
                .map(schedule -> DoctorScheduleResponse.builder()
                        .id(schedule.getId())
                        .dateTime(schedule.getDate())
                        .vaccine(schedule.getVaccine().getName())
                        .firstName(schedule.getOrder().getChild().getFirstName())
                        .lastName(schedule.getOrder().getChild().getLastName())
                        .status(schedule.getStatus())
                        .build())
                .toList();
    }

    public List<DoctorScheduleResponse> getDoctorHistory(Integer doctorId) {
        return vaccineScheduleRepository.findByDoctorId(doctorId).stream()
                .filter(schedule -> (schedule.getStatus() == VaccineScheduleStatus.COMPLETED || schedule.getStatus() == VaccineScheduleStatus.CANCELLED))
                .map(schedule -> DoctorScheduleResponse.builder()
                        .id(schedule.getId())
                        .dateTime(schedule.getDate())
                        .vaccine(schedule.getVaccine().getName())
                        .firstName(schedule.getOrder().getChild().getFirstName())
                        .lastName(schedule.getOrder().getChild().getLastName())
                        .status(schedule.getStatus())
                        .build())
                .toList();
    }

    @Override
    public ScheduleDetail getScheduleDetails(Integer detailId) {
        VaccineSchedule schedule = vaccineScheduleRepository.findById(detailId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch với ID: " + detailId));

        return ScheduleDetail.builder()
                .id(schedule.getId())
                .feedback(schedule.getFeedback())
                .date(schedule.getDate())
                .doctorName(schedule.getDoctor().getFullName())
                .child(ChildDetail.builder()
                        .id(schedule.getOrder().getChild().getId())
                        .firstName(schedule.getOrder().getChild().getFirstName())
                        .lastName(schedule.getOrder().getChild().getLastName())
                        .dob(schedule.getOrder().getChild().getDob())
                        .gender(String.valueOf(schedule.getOrder().getChild().getGender()))
                        .weight(schedule.getOrder().getChild().getWeight())
                        .height(schedule.getOrder().getChild().getHeight())
                        .bloodType(schedule.getOrder().getChild().getBloodType())
                        .healthNotes(schedule.getOrder().getChild().getHealthNote())
                        .build())
                .order(OrderDetail.builder()
                        .id(schedule.getOrder().getId())
                        .bookDate(schedule.getOrder().getBookDate())
                        .serviceType(String.valueOf(schedule.getOrder().getServiceType()))
                        .build())
                .vaccine(VaccineDetail.builder()
                        .id(schedule.getVaccine().getId())
                        .name(schedule.getVaccine().getName())
                        .vaccineCode(schedule.getVaccine().getVaccineCode())
                        .description(schedule.getVaccine().getDescription())
                        .build())
                .reactions(schedule.getReactions().stream()
                        .sorted(Comparator.comparing(Reaction::getDate).reversed())
                        .map(reaction -> ReactionDetail.builder()
                                .date(reaction.getDate())
                                .reaction(reaction.getReaction())
                                .build())
                        .toList())
                .status(schedule.getStatus())
                .pastSchedules(schedule.getOrder().getChild().getSchedules().stream()
                        .filter(s -> s.getStatus() == VaccineScheduleStatus.COMPLETED)
                        .map(pastSchedule -> PastSchedule.builder()
                                .id(pastSchedule.getId())
                                .date(pastSchedule.getDate())
                                .vaccineName(pastSchedule.getVaccine().getName())
                                .doctorId(pastSchedule.getDoctor().getId().toString())
                                .feedback(pastSchedule.getFeedback())
                                .comboName(pastSchedule.getCombo() != null ? pastSchedule.getCombo().getName() : null)
                                .reactions(pastSchedule.getReactions().stream()
                                        .map(reaction -> ReactionDetail.builder()
                                                .date(reaction.getDate())
                                                .reaction(reaction.getReaction())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public Object confirmVaccination(Integer scheduleId, Integer doctorId) {
        // Tìm lịch tiêm theo ID
        VaccineSchedule schedule = vaccineScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lịch với ID: " + scheduleId));

        // Kiểm tra lịch đã được xác nhận trước đó chưa
        if (schedule.getStatus() == VaccineScheduleStatus.COMPLETED) {
            throw new BadRequestException("Lịch đã được hoàn thành.");
        }

        // Lấy danh sách giao dịch xuất vaccine của bác sĩ trong hôm nay
        List<Transaction> transactions = transactionRepository.findByDoctorId(
                        doctorId).stream()
                .filter(transaction -> transaction.getDate().toLocalDate().equals(LocalDate.now()))
                .toList();

        if (transactions.isEmpty()) {
            throw new BadRequestException("Không tìm thấy giao dịch vaccine cho bác sĩ này trong ngày.");
        }

        BatchTransaction selectedBatchTransaction = transactions.stream()
                .flatMap(transaction -> transaction.getBatchTransactions().stream())
                .filter(bt -> bt.getBatch().getVaccine().getId().equals(schedule.getVaccine().getId()))
                .filter(bt -> bt.getRemaining() > 0)
                .min(Comparator.comparing(bt -> bt.getBatch().getExpiration()))
                .orElseThrow(() -> new BadRequestException("Không tìm thấy lô vaccine phù hợp."));

        selectedBatchTransaction.setRemaining(selectedBatchTransaction.getRemaining() - 1);
        batchTransactionRepository.save(selectedBatchTransaction);

        // Cập nhật trạng thái lịch tiêm
        schedule.setStatus(VaccineScheduleStatus.COMPLETED);
        vaccineScheduleRepository.save(schedule);
        return null;
    }


    @Override
    @Transactional
    public void handleCallback(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId)
        );
        List<VaccineSchedule> draftSchedules = vaccineScheduleRepository.findByStatusAndChildId(VaccineScheduleStatus.DRAFT, order.getChild().getId());
        for (VaccineSchedule schedule : draftSchedules) {
            schedule.setStatus(VaccineScheduleStatus.PLANNED);
            schedule.setOrder(order);
        }
        vaccineScheduleRepository.saveAll(draftSchedules);
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    @Override
    public List<VaccineScheduleDTO> getDrafts(Integer childId) {
        List<VaccineSchedule> drafts = vaccineScheduleRepository.findByStatusAndChildId(VaccineScheduleStatus.DRAFT, childId);
        return drafts.stream().map(
                VaccineScheduleDTO::fromEntity
        ).toList();
    }

    private LocalDateTime getEarliestDatePossible(Child child) {

        // Get list of schedules of child sorted by order descending (latest date comes first)
        List<VaccineSchedule> childSchedules = vaccineScheduleRepository.findByChildIdOrderByDateDesc(child.getId()).stream().filter(
                v -> v.getStatus() != VaccineScheduleStatus.CANCELLED
        ).toList();

        // If child doesn't have previous schedules, then the earliest date you can vaccinate is today
        if (childSchedules.isEmpty()) {
            System.out.println("Trẻ không có lịch tiêm trước, có thể tiêm thoải mái");
            return LocalDateTime.now();
        }

        // Get latest schedule
        VaccineSchedule latestSchedule = childSchedules.getFirst();
        System.out.println("Lịch cuối cùng của trẻ là vào ngày " + latestSchedule.getDate());

        return latestSchedule.getDate().plusDays(
                latestSchedule.getVaccine().isActivated()
                        ? intervalAfterActiveVaccine
                        : intervalAfterInactiveVaccine
        );
    }

    @Override
    @Transactional
    public List<VaccineScheduleDTO> draftSchedule(VaccineDraftRequest request) {

        // Deleting previous drafts
        deleteDraftSchedules(request.childId());

        // Find child and doctor
        Child child = childrenRepository.findById(request.childId()).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy trẻ em với ID: " + request.childId()));
        User doctor = userRepository.findById(request.doctorId()).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bác sĩ với ID: " + request.doctorId()));
        User customer = userRepository.findById(request.customerId()).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + request.customerId()));

        List<VaccineSchedule> schedules;

        if (request.serviceType() == ServiceType.SINGLE) {

            // Get vaccines from ids
            List<Vaccine> vaccines = request.ids().stream().map(id -> vaccineRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                    () -> new EntityNotFoundException("Không tìm thấy vaccine có ID: " + id)
            )).toList();

            LocalDateTime fourteenDaysAfter = LocalDateTime.now().plusDays(14);
            if (request.desiredDate().isBefore(fourteenDaysAfter)) {
                AppointmentVerificationResponse context = appointmentVerificationService.verifyAppointmentAvailability(vaccines.getFirst().getId(), request.desiredDate());
                if (!context.isAvailable() && !context.isCanBeRescheduled()) {
                    throw new BadRequestException(context.getMessage());
                }
            }

            // Get earliest date possible vs. desired date
            LocalDateTime earliestPossibleDate = getEarliestDatePossible(child);
            LocalDateTime firstDate = request.desiredDate().isAfter(earliestPossibleDate) ? request.desiredDate() : earliestPossibleDate;

            // Draft combo schedules
            schedules = draftVaccineSchedules(doctor, child, customer, firstDate, vaccines);
        } else {

            // Get vaccines from combos
            List<Combo> combos = request.ids().stream().map(id -> comboRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                    () -> new EntityNotFoundException("Không tìm thấy combo có ID: " + id)
            )).toList();

            LocalDateTime fourteenDaysAfter = LocalDateTime.now().plusDays(14);
            if (request.desiredDate().isBefore(fourteenDaysAfter)) {
                AppointmentVerificationResponse context = appointmentVerificationService.verifyAppointmentAvailability(combos.getFirst().getVaccineCombos().getFirst().getVaccine().getId(), request.desiredDate());
                if (!context.isAvailable() && !context.isCanBeRescheduled()) {
                    throw new BadRequestException(context.getMessage());
                }
            }

            // Get earliest date possible vs. desired date
            LocalDateTime earliestPossibleDate = getEarliestDatePossible(child);
            LocalDateTime firstDate = request.desiredDate().isAfter(earliestPossibleDate) ? request.desiredDate() : earliestPossibleDate;

            // Draft vaccine schedules
            schedules = draftComboSchedules(doctor, child, customer, firstDate, combos);
        }
        return schedules.stream().map(VaccineScheduleDTO::fromEntity).toList();
    }

    @Override
    @Transactional
    public List<VaccineSchedule> draftComboSchedules(User doctor, Child child, User customer, LocalDateTime firstDate, List<Combo> combos) {
        // Notes of shift schedules
        StringBuilder notesBuilder = new StringBuilder();

        // Get first date
        LocalDateTime date = firstDate;
        List<VaccineSchedule> schedules = new ArrayList<>();

        // Save last combo to find the starting point of combo
        Combo lastCombo = null;
        for (Combo combo : combos) {

            // Position of vaccine in schedule
            int orderInCombo = 1;

            // Calculate the distance between two different combos
            if (lastCombo != null && !lastCombo.getId().equals(combo.getId())) {

                // Get interval between two different combos
                date = getComboInterval(notesBuilder, date, lastCombo, combo);
            }

            // Get each vaccine in combo
            List<VaccineCombo> vaccineCombos = combo.getVaccineCombos();
            Vaccine currentVaccine = null;
            for (VaccineCombo vaccineCombo : vaccineCombos) {

                // Check if child is qualified to vaccinate
                if (currentVaccine == null || !currentVaccine.getId().equals(vaccineCombo.getVaccine().getId())) {
                    currentVaccine = vaccineCombo.getVaccine();
                    date = checkRequiredVaccines(notesBuilder, child, date, currentVaccine);
                }

                // Add interval
                date = date.plusDays(vaccineCombo.getIntervalDays());

                // Shift schedule if doctor is busy
                while (vaccineScheduleRepository.existsByDoctorAndDate(doctor, date)) {
                    notesBuilder
                            .append("Bác sĩ")
                            .append(doctor.getFullName())
                            .append(" đã có lịch vào ")
                            .append(date)
                            .append(", dời sang ");
                    date = date.plusMinutes(30);
                    if (date.toLocalTime().isAfter(LocalTime.of(20, 0))) {
                        date = date.plusDays(1).with(LocalTime.of(8, 0));
                    }
                    notesBuilder.append(date)
                            .append("\n");
                }
                VaccineSchedule vaccineSchedule = VaccineSchedule.builder()
                        .id(null)
                        .date(date)
                        .status(VaccineScheduleStatus.DRAFT)
                        .feedback(null)
                        .vaccine(vaccineCombo.getVaccine())
                        .doctor(doctor)
                        .combo(combo)
                        .child(child)
                        .customer(customer)
                        .orderNo(orderInCombo++)
                        .notes(notesBuilder.toString())
                        .build();
                schedules.add(vaccineScheduleRepository.save(vaccineSchedule));
                notesBuilder = new StringBuilder();
            }
            lastCombo = combo;
        }
        return schedules;
    }

    private LocalDateTime checkRequiredVaccines(StringBuilder notesBuilder, Child child, LocalDateTime date, Vaccine currentVaccine) {
        System.out.println("Kế hoạch: Tiêm " + currentVaccine.getName() + " vào ngày " + date);
        List<VaccineInterval> requiredVaccines = currentVaccine.getToVaccineIntervals();
        LocalDateTime finalDate = date;

        for (VaccineInterval interval : requiredVaccines) {
            LocalDateTime requiredDate = date.minusDays(interval.getDaysBetween());
            System.out.format("%s yêu cầu %s cần được tiêm trước %s: \n",
                    currentVaccine.getName(),
                    interval.getFromVaccine().getName(),
                    requiredDate);

            List<VaccineSchedule> childHistoryVaccinations = vaccineScheduleRepository
                    .findByChildIdAndVaccineIdOrderByDateAsc(child.getId(), interval.getFromVaccine().getId());

            if (childHistoryVaccinations.isEmpty()) {
                System.err.println("Trẻ chưa từng tiêm mũi " + interval.getFromVaccine().getName());
                throw new BadRequestException("Trẻ chưa từng tiêm mũi " + interval.getFromVaccine().getName());
            }

            // Lấy mũi tiêm cần thiết
            VaccineSchedule vaccineSchedule = childHistoryVaccinations.getLast();
            LocalDateTime validDate = vaccineSchedule.getDate().plusDays(interval.getDaysBetween());

            if (!validDate.isBefore(finalDate)) {
                notesBuilder.append(String.format("Dời lịch tiêm %s thành ngày %s để đáp ứng điều kiện cho %s\n",
                        currentVaccine.getName(),
                        validDate,
                        interval.getFromVaccine().getName()));
                finalDate = validDate;
            }
        }

        if (child.getAge(finalDate.toLocalDate()) >= currentVaccine.getMinAge() && child.getAge(finalDate.toLocalDate()) <= currentVaccine.getMaxAge()) {
            notesBuilder
                    .append("Trẻ tuổi: ")
                    .append(child.getAge(finalDate.toLocalDate()))
                    .append(" thoả mãn độ tuổi để tiêm ( ")
                    .append(currentVaccine.getMinAge())
                    .append(" - ")
                    .append(currentVaccine.getMaxAge())
                    .append(")\n");
        } else {
            throw new BadRequestException("Trẻ " + child.getAge(finalDate.toLocalDate()) + " tuổi không thoả mãn độ tuổi để tiêm vaccine " + currentVaccine.getName() + " cần " + currentVaccine.getMinAge() + " - " + currentVaccine.getMaxAge() + " tuổi");
        }

        return finalDate;
    }

    private LocalDateTime getComboInterval(StringBuilder notesBuilder, LocalDateTime date, Combo lastCombo, Combo newCombo) {

        // Get last vaccine from the last combo
        Vaccine lastVaccineFromLastCombo = lastCombo.getVaccineCombos().getLast().getVaccine();

        // Get the first vaccine from the currentCombo
        Vaccine firstVaccineFromCurrentCombo = newCombo.getVaccineCombos().getFirst().getVaccine();

        // Get find if there is interval between vaccine of previous combo
        VaccineIntervalId id = VaccineIntervalId.builder()
                .fromVaccineId(lastVaccineFromLastCombo.getId())
                .toVaccineId(firstVaccineFromCurrentCombo.getId())
                .build();

        Optional<VaccineInterval> interval = vaccineIntervalRepository.findById(id);
        if (interval.isPresent()) {
            notesBuilder
                    .append("Combo ")
                    .append(lastCombo.getName())
                    .append(" --> ")
                    .append("Combo ")
                    .append(newCombo.getName())
                    .append(": Yêu cầu khoảng cách ")
                    .append(interval.get().getDaysBetween())
                    .append(" ngày, dời từ ")
                    .append(date);
            date = date.plusDays(interval.get().getDaysBetween());
            notesBuilder.append(" sang ngày ")
                    .append(date)
                    .append("\n");
        } else {
            if (lastVaccineFromLastCombo.isActivated()) {
                notesBuilder
                        .append("Combo ")
                        .append(lastCombo.getName())
                        .append(" --> ")
                        .append("Combo ")
                        .append(newCombo.getName())
                        .append(": Vaccine ")
                        .append(lastVaccineFromLastCombo.getName())
                        .append(" là vaccine sống, yêu cầu khoảng cách ")
                        .append(intervalAfterActiveVaccine)
                        .append(" ngày, dời từ ")
                        .append(date);
                date = date.plusDays(intervalAfterActiveVaccine);
                notesBuilder
                        .append(" sang ngày ")
                        .append(date)
                        .append("\n");
            } else {
                notesBuilder
                        .append("Combo ")
                        .append(lastCombo.getName())
                        .append(" --> ")
                        .append("Combo ")
                        .append(newCombo.getName())
                        .append(": Vaccine ")
                        .append(lastVaccineFromLastCombo.getName())
                        .append(" là vaccine bất hoạt, yêu cầu khoảng cách ")
                        .append(intervalAfterInactiveVaccine)
                        .append(" ngày, dời từ ")
                        .append(date);
                date = date.plusDays(intervalAfterInactiveVaccine);
                notesBuilder
                        .append(" sang ngày ")
                        .append(date)
                        .append("\n");
            }
        }
        return date;
    }

    private LocalDateTime getVaccineInterval(StringBuilder notesBuilder, LocalDateTime date, Vaccine lastVaccine, Vaccine vaccine) {
        // Get find if there is interval between vaccine of previous combo/order
        VaccineIntervalId id = VaccineIntervalId.builder()
                .fromVaccineId(lastVaccine.getId())
                .toVaccineId(vaccine.getId())
                .build();
        Optional<VaccineInterval> interval = vaccineIntervalRepository.findById(id);
        if (interval.isPresent()) {
            notesBuilder
                    .append("Vaccine ")
                    .append(lastVaccine.getName())
                    .append(" --> ")
                    .append("Vaccine ")
                    .append(vaccine.getName())
                    .append(": Yêu cầu khoảng cách ")
                    .append(interval.get().getDaysBetween())
                    .append(" ngày, dời từ ")
                    .append(date);
            date = date.plusDays(interval.get().getDaysBetween());
            notesBuilder.append(" sang ngày ")
                    .append(date)
                    .append("\n");
        } else {
            if (lastVaccine.isActivated()) {
                notesBuilder
                        .append("Vaccine ")
                        .append(lastVaccine.getName())
                        .append(" --> ")
                        .append("Vaccine ")
                        .append(vaccine.getName())
                        .append(": Vaccine ")
                        .append(vaccine.getName())
                        .append(" là vaccine sống, yêu cầu khoảng cách ")
                        .append(intervalAfterActiveVaccine)
                        .append(" ngày, dời từ ")
                        .append(date);
                date = date.plusDays(intervalAfterActiveVaccine);
                notesBuilder.append(" sang ngày ")
                        .append(date)
                        .append("\n");
            } else {
                notesBuilder
                        .append("Vaccine ")
                        .append(lastVaccine.getName())
                        .append(" --> ")
                        .append("Vaccine ")
                        .append(vaccine.getName())
                        .append(": Vaccine ")
                        .append(vaccine.getName())
                        .append(" là vaccine bất hoạt, yêu cầu khoảng cách ")
                        .append(intervalAfterInactiveVaccine)
                        .append(" ngày, dời từ ")
                        .append(date);
                date = date.plusDays(intervalAfterInactiveVaccine);
                notesBuilder.append(" sang ngày ")
                        .append(date)
                        .append("\n");
            }
        }
        return date;
    }

    @Override
    @Transactional
    public List<VaccineSchedule> draftVaccineSchedules(User doctor, Child child, User customer, LocalDateTime firstDate, List<Vaccine> vaccines) {
        // Note out the required vaccines
        StringBuilder notesBuilder = new StringBuilder();

        // Get first date
        LocalDateTime date = firstDate;
        List<VaccineSchedule> schedules = new ArrayList<>();

        // Note last vaccine to find interval between vaccines
        Vaccine lastVaccine = null;
        for (Vaccine vaccine : vaccines) {

            // Calculate the distance between two different vaccines
            if (lastVaccine != null && !vaccine.getId().equals(lastVaccine.getId())) {
                date = getVaccineInterval(notesBuilder, date, lastVaccine, vaccine);
            }

            // Vào mỗi loại vaccine, kiểm tra xem vaccine đó có đủ tiêu chuẩn tiêm không
            date = checkRequiredVaccines(notesBuilder, child, date, vaccine);

            int dose = vaccine.getDose();
            for (int i = 0; i < dose; i++) {
                System.err.println(i);
                VaccineTiming vaccineTiming = vaccineTimingService.getVaccineTimingByVaccine(vaccine, i + 1);
                date = date.plusDays(vaccineTiming.getIntervalDays());
                if (vaccineScheduleRepository.existsByDoctorAndDate(doctor, date)) {
                    notesBuilder
                            .append("Bác sĩ").append(doctor.getFullName()).append(" đã có lịch vào ")
                            .append(date)
                            .append(", dời sang ");
                    date = date.plusMinutes(30);
                    if (date.toLocalTime().isAfter(LocalTime.of(20, 0))) {
                        date = date.plusDays(1).with(LocalTime.of(8, 0));
                    }
                    notesBuilder.append(date)
                            .append("\n");
                }
                VaccineSchedule vaccineSchedule = VaccineSchedule.builder()
                        .id(null)
                        .date(date)
                        .status(VaccineScheduleStatus.DRAFT)
                        .feedback(null)
                        .vaccine(vaccine)
                        .orderNo(i + 1)
                        .doctor(doctor)
                        .child(child)
                        .notes(notesBuilder.toString())
                        .customer(customer)
                        .build();
                schedules.add(vaccineSchedule);
                notesBuilder = new StringBuilder();
            }
            lastVaccine = vaccine;
        }
        schedules = vaccineScheduleRepository.saveAll(schedules);
        return schedules;
    }

    @Override
    public PagingResponse getVaccinesByCustomer(Integer customerId, PagingRequest pagingRequest) {
        Pageable pageable = PaginationUtil.getPageable(pagingRequest);
        User customer = userRepository.findByIdAndDeletedIsFalse(customerId).orElseThrow(
                () -> new BadRequestException("Không tìm thấy khách hàng với ID: " + customerId)
        );
        Page<VaccineSchedule> vaccineSchedules = vaccineScheduleRepository.findByCustomer(customer, pageable);
        return PagingResponse.builder()
                .code(HttpStatus.OK.toString())
                .message("Đã tìm thấy lịch tiêm vaccine")
                .currentPage(vaccineSchedules.getNumber() + 1)
                .totalPages(vaccineSchedules.getTotalPages())
                .pageSize(vaccineSchedules.getSize())
                .totalElements(vaccineSchedules.getTotalElements())
                .sortingOrders(pagingRequest.getSortBy().split(","))
                .data(vaccineSchedules.getContent().stream().map(
                        CustomerScheduleResponse::fromEntity
                ).toList())
                .build();
    }

}
