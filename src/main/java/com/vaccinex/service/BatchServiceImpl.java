package com.vaccinex.service;

import com.vaccinex.base.exception.IdNotFoundException;
import com.vaccinex.dao.*;
import com.vaccinex.dto.paging.BatchPagingResponse;
import com.vaccinex.dto.paging.PagingRequest;
import com.vaccinex.dto.request.BatchCreateRequest;
import com.vaccinex.dto.request.BatchUpdateRequest;
import com.vaccinex.dto.request.VaccineReturnRequest;
import com.vaccinex.dto.response.BatchQuantityDTO;
import com.vaccinex.pojo.Batch;
import com.vaccinex.pojo.BatchTransaction;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.enums.Shift;
import com.vaccinex.utils.PaginationUtil;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Stateless
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {

    private final BatchDao batchRepository;
    private final BatchTransactionDao batchTransactionRepository;
    private final VaccineDao vaccineRepository;
    private final VaccineScheduleDao vaccineScheduleRepository;
    private final TransactionDao transactionRepository;

    private Vaccine getVaccine(Integer vaccineId) {
        return vaccineRepository
                .findByIdAndDeletedIsFalse(vaccineId)
                .orElseThrow(() -> new IdNotFoundException("Vaccine with ID " + vaccineId + " not found"));
    }

    @Override
    public MappingJacksonValue getAllBatches(PagingRequest request) {
        Pageable pageable = PaginationUtil.getPageable(request);
        Map<String, String> maps = request.getFilters();
        maps.remove("pageNo");
        maps.remove("pageSize");
        maps.remove("params");
        maps.remove("sortBy");
        Specification<Batch> specification = BatchPagingResponse.filterByFields(maps);
        Page<Batch> batches = batchRepository.findAll(specification, pageable);
        List<BatchPagingResponse> mappedDTO = batches.getContent().stream().map(BatchPagingResponse::fromEntity).toList();
        return PaginationUtil.getPagedMappingJacksonValue(request, batches, mappedDTO, "Lấy tất cả lô vaccine");
    }

    @Override
    public Batch getBatchById(Integer id) {
        return batchRepository
                .findByIdAndDeletedIsFalse(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("Không tìm thấy lô vaccine với ID " + id)
                );
    }

    @Override
    public List<BatchQuantityDTO> getQuantityOfVaccines() {
        List<BatchQuantityDTO> batchesByQuantity = new ArrayList<>();
        for (Vaccine vaccine : vaccineRepository.findByDeletedIsFalse()) {
            List<Batch> batches = vaccine.getBatches().stream().filter(b -> !b.isDeleted()).toList();
            int totalQuantity = 0;
            int scheduledQuantity = 0;
            int expiredQuantity = 0;
            int quantityAboutToBeExpired = 0;
            LocalDateTime dateAboutToBeExpired = LocalDateTime.now().plusDays(7);
            LocalDateTime latestExpiresIn = LocalDateTime.now().plusYears(999);
            for (Batch batch : batches) {
                if (batch.getExpiration().isBefore(LocalDateTime.now())) {
                    expiredQuantity += batch.getQuantity();
                } else {
                    if (batch.getExpiration().isBefore(latestExpiresIn)) {
                        latestExpiresIn = batch.getExpiration();
                    }
                    if (batch.getExpiration().isBefore(dateAboutToBeExpired)) {
                        quantityAboutToBeExpired = batch.getQuantity();
                        dateAboutToBeExpired = batch.getExpiration();
                    } else if (batch.getExpiration().equals(dateAboutToBeExpired)) {
                        quantityAboutToBeExpired += batch.getQuantity();
                    }
                    totalQuantity += batch.getQuantity();
                    int usedQuantity = batch.getBatchTransactions().stream().mapToInt(BatchTransaction::getQuantityTaken).sum();
                    int plannedQuantity = vaccineScheduleRepository.countBatch(batch.getId());
                    scheduledQuantity += (plannedQuantity - usedQuantity);
                }
            }
            BatchQuantityDTO batchQuantityDTO = BatchQuantityDTO.builder()
                    .vaccineId(vaccine.getId())
                    .vaccineCode(vaccine.getVaccineCode())
                    .expiredQuantity(expiredQuantity)
                    .totalQuantity(totalQuantity)
                    .scheduledQuantity(scheduledQuantity)
                    .quantityAboutToBeExpired(quantityAboutToBeExpired)
                    .dateAboutToBeExpired(dateAboutToBeExpired)
                    .latestExpiresIn(latestExpiresIn)
                    .build();
            batchesByQuantity.add(batchQuantityDTO);
        }
        return batchesByQuantity;
    }

    @Override
    public void createBatch(BatchCreateRequest request) {
        Vaccine vaccine = getVaccine(request.vaccineId());

        Batch batch = Batch.builder()
                .batchCode(request.batchCode())
                .vaccine(vaccine)
                .quantity(request.batchSize())
                .batchSize(request.batchSize())
                .manufactured(request.manufactured())
                .imported(LocalDateTime.now())
                .expiration(request.manufactured().plusDays(vaccine.getExpiresInDays()))
                .distributer(request.distributer())
                .build();
        batchRepository.save(batch);
    }

    @Override
    public void updateBatch(Integer batchId, BatchUpdateRequest request) {
        Vaccine vaccine = getVaccine(request.vaccineId());

        Batch batch = getBatchById(batchId);
        batch.setBatchCode(request.batchCode());
        batch.setVaccine(vaccine);
        batch.setQuantity(request.batchSize());
        batch.setBatchSize(request.batchSize());
        batch.setManufactured(request.manufactured());
        batch.setImported(request.imported());
        batch.setExpiration(request.expiration());
        batch.setDistributer(request.distributer());
        batchRepository.save(batch);
    }

    @Override
    public void deleteBatch(Integer batchId) {
        Batch batch = getBatchById(batchId);
        batch.setDeleted(true);
        batchRepository.save(batch);
    }

    @Override
    @Transactional
    public void returnVaccine(VaccineReturnRequest request) {
        List<Transaction> transactions;
        LocalDate today = LocalDate.now();
        if (request.getShift() == Shift.AFTERNOON) {
            transactions = transactionRepository.findByDoctorIdAndDateAfterAndDateBefore(
                    request.getDoctorId(),
                    today.atTime(12, 1),
                    today.atTime(20, 0)
            );
            if (transactions.isEmpty()) {
                throw new BadRequestException("Không tìm thấy lần xuất vaccine nào trong ca làm việc buổi chiều của bác sĩ ID " + request.getDoctorId());
            }
        } else {
            transactions = transactionRepository.findByDoctorIdAndDateAfterAndDateBefore(
                    request.getDoctorId(),
                    today.atTime(8, 0),
                    today.atTime(12, 0)
            );
            if (transactions.isEmpty()) {
                throw new BadRequestException("Không tìm thấy lần xuất vaccine nào trong ca làm việc buổi sáng của bác sĩ ID " + request.getDoctorId());
            }
        }
        List<BatchTransaction> batchTransactions = transactions.stream().flatMap(t -> t.getBatchTransactions().stream()).toList();
        List<BatchTransaction> modifiedBTs = new ArrayList<>();
        List<Batch> modifiedBatches = new ArrayList<>();
        for (VaccineReturnRequest.VaccinesQuantity vq : request.getReturned()) {
            Vaccine vaccine = vaccineRepository.findById(vq.getVaccineId()).orElseThrow(
                    () -> new BadRequestException("Không tìm thấy vaccine với ID " + vq.getVaccineId())
            );

            // Filter and sort batch transactions by expiration DESC
            List<BatchTransaction> filteredTransactions = batchTransactions.stream()
                    .filter(bt -> bt.getBatch().getVaccine().equals(vaccine)) // Filter by vaccine
                    .filter(bt -> bt.getRemaining() > 0)  // Find used batches
                    .sorted(Comparator.comparing(bt -> bt.getBatch().getExpiration(), Comparator.nullsLast(Comparator.reverseOrder()))) // Sort expiration DESC
                    .toList();

            int quantityToReturn = vq.getQuantity();

            for (BatchTransaction bt : filteredTransactions) {
                Batch batch = bt.getBatch();
                int remaining = bt.getRemaining();

                // Exit if no vaccines left to return
                if (quantityToReturn <= 0) {
                    break;
                }

                // Ensure we don’t exceed remaining
                int addBack = Math.max(
                        Math.max(quantityToReturn, remaining)
                        , batch.getBatchSize() - batch.getQuantity()
                );
                bt.setRemaining(bt.getRemaining() - addBack);
                batch.setQuantity(batch.getQuantity() + addBack);
                modifiedBTs.add(bt);
                quantityToReturn -= addBack;
            }

            // This ensures the doctor is not returning more than they initially took.
            if (quantityToReturn > 0) {
                throw new BadRequestException("Số lượng trả về vượt quá số lượng đã lấy cho vaccine ID " + vq.getVaccineId());
            }
        }
        batchRepository.saveAll(modifiedBatches);
        batchTransactionRepository.saveAll(modifiedBTs);
    }
}
