package com.vaccinex.service;

import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.base.exception.EntityNotFoundException;
import com.vaccinex.dao.*;
import com.vaccinex.dto.request.ExportVaccineRequest;
import com.vaccinex.dto.request.VaccineRequest;
import com.vaccinex.dto.response.VaccineReportResponseDTO;
import com.vaccinex.mapper.VaccineMapper;
import com.vaccinex.pojo.*;
import com.vaccinex.pojo.composite.BatchTransactionId;
import com.vaccinex.pojo.enums.TransactionType;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Stateless
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {


    @Inject
    private VaccineScheduleDao vaccineScheduleRepository;

    @Inject
    private VaccineDao vaccineRepository;

    @Inject
    private VaccineMapper vaccineMapper;

    @Inject
    private UserDao userRepository;

    @Inject
    private TransactionDao transactionRepository;

    @Inject
    private BatchDao batchRepository;

    @Inject
    private BatchTransactionDao batchTransactionRepository;

    /**
     * Report required vaccine doses for morning and afternoon sessions
     */
    @Override
    public List<VaccineReportResponseDTO> getVaccineReports(Integer doctorId, String shift, LocalDate date) {
        LocalTime noon = LocalTime.NOON;

        // Lấy tất cả lịch hẹn và lọc theo doctorId, ngày hiện tại và ca trực
        List<VaccineSchedule> schedules = vaccineScheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getDoctor().getId().equals(doctorId)) // Check doctorId
                .filter(schedule -> schedule.getDate().toLocalDate().isEqual(date)) // Check ngày hiện tại
                .filter(schedule -> schedule.getStatus().equals(VaccineScheduleStatus.PLANNED)) // Check trạng thái đã xác nhận
                .filter(schedule -> {
                    LocalTime scheduleTime = schedule.getDate().toLocalTime();
                    return "morning".equalsIgnoreCase(shift) == scheduleTime.isBefore(noon);
                })
                .toList();

        // Đếm số lượng vaccine được sử dụng
        Map<Integer, Integer> vaccineCountMap = new HashMap<>();
        for (VaccineSchedule schedule : schedules) {
            Integer vaccineId = schedule.getVaccine().getId();
            vaccineCountMap.put(vaccineId, vaccineCountMap.getOrDefault(vaccineId, 0) + 1);
        }

        // Lấy thông tin vaccine và map sang DTO
        List<VaccineReportResponseDTO> vaccineReports = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : vaccineCountMap.entrySet()) {
            Optional<Vaccine> vaccineOpt = vaccineRepository.findById(entry.getKey());
            vaccineOpt.ifPresent(vaccine -> {
                VaccineReportResponseDTO vaccineDTO = vaccineMapper.vaccineToVaccineReportDTO(vaccine);
                vaccineDTO.setQuantity(entry.getValue()); // Set số lượng
                vaccineReports.add(vaccineDTO);
            });
        }

        return vaccineReports;
    }

    @Override
    @Transactional
    public Object requestVaccineExport(ExportVaccineRequest request) throws BadRequestException {
        // Tìm doctor
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found."));

        Transaction transaction = Transaction.builder()
                .date(LocalDateTime.now())
                .type(TransactionType.EXPORT)
                .doctor(doctor)
                .build();
        transaction = transactionRepository.save(transaction);

        for (VaccineRequest vaccineRequest : request.getVaccines()) {
            Integer vaccineId = vaccineRequest.getVaccineId();
            Integer quantityRequested = vaccineRequest.getQuantity();

            // Tìm các batch còn số lượng, chưa hết hạn, sắp xếp theo ngày hết hạn tăng dần
            List<Batch> availableBatches = batchRepository.findAll().stream()
                    .filter(batch -> batch.getVaccine().getId().equals(vaccineId))
                    .filter(batch -> batch.getQuantity() > 0)
                    .filter(batch -> batch.getExpiration().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Batch::getExpiration)) // FIFO
                    .toList();

            if (availableBatches.isEmpty()) {
                throw new BadRequestException("No available vaccine batches for vaccine ID: " + vaccineId);
            }

            int quantityToBeTaken = quantityRequested;

            // Xuất vaccine từ nhiều batch nếu cần
            for (Batch batch : availableBatches) {
                if (quantityToBeTaken <= 0) break;

                int takenQuantity = Math.min(batch.getQuantity(), quantityToBeTaken);
                batch.setQuantity(batch.getQuantity() - takenQuantity);
                quantityToBeTaken -= takenQuantity;

                batch = batchRepository.save(batch);
                BatchTransaction batchTransaction = BatchTransaction.builder()
                        .id(BatchTransactionId.builder()
                                .batchId(batch.getId())
                                .transactionId(transaction.getId())
                                .build())
                        .batch(batch)
                        .transaction(transaction)
                        .quantityTaken(takenQuantity)
                        .remaining(takenQuantity)
                        .build();
                batchTransactionRepository.save(batchTransaction);
            }

            // Nếu không đủ số lượng, rollback và báo lỗi
            if (quantityToBeTaken > 0) {
                throw new BadRequestException("Not enough vaccines to export for vaccine ID: " + vaccineId);
            }
        }

        // Nếu tất cả vaccine đều hợp lệ, lưu dữ liệu vào DB

       return "Vaccine export request has been successfully processed.";
    }


}
