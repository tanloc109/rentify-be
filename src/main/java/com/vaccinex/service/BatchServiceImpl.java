package com.vaccinex.service;

import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.base.exception.IdNotFoundException;
import com.vaccinex.dao.*;
import com.vaccinex.dto.response.BatchResponse;
import com.vaccinex.dto.request.BatchCreateRequest;
import com.vaccinex.dto.request.BatchUpdateRequest;
import com.vaccinex.dto.request.VaccineReturnRequest;
import com.vaccinex.dto.response.BatchQuantityDTO;
import com.vaccinex.pojo.Batch;
import com.vaccinex.pojo.BatchTransaction;
import com.vaccinex.pojo.Transaction;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.enums.Shift;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
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

    @Inject
    private BatchDao batchRepository;

    @Inject
    private BatchTransactionDao batchTransactionRepository;

    @Inject
    private VaccineDao vaccineRepository;

    @Inject
    private VaccineScheduleDao vaccineScheduleRepository;

    @Inject
    private TransactionDao transactionRepository;

    private Vaccine getVaccine(Integer vaccineId) {
        return vaccineRepository
                .findByIdAndDeletedIsFalse(vaccineId)
                .orElseThrow(() -> new IdNotFoundException("Vaccine with ID " + vaccineId + " not found"));
    }

    @Override
    public List<BatchResponse> getAllBatches() {
        List<Batch> batches = batchRepository.findAll();
        return batches.stream().map(BatchResponse::fromEntity).toList();
    }

    private boolean applyFilters(Batch batch, Map<String, String> filters) {
        // Implement your filtering logic here based on the filters map
        // Example:
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isEmpty()) {
                continue;
            }

            switch (key) {
                case "batchCode":
                    if (!batch.getBatchCode().toLowerCase().contains(value.toLowerCase())) {
                        return false;
                    }
                    break;
                case "vaccineId":
                    if (!batch.getVaccine().getId().toString().equals(value)) {
                        return false;
                    }
                    break;
                case "vaccineName":
                    if (!batch.getVaccine().getName().toLowerCase().contains(value.toLowerCase())) {
                        return false;
                    }
                    break;
                // Add more filter conditions as needed
            }
        }
        return true;
    }

    @Override
    public Batch getBatchById(Integer id) {
        return batchRepository
                .findByIdAndDeletedIsFalse(id)
                .orElseThrow(
                        () -> new IdNotFoundException("Batch with ID " + id + " not found")
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
        if (request.getShift().equals(Shift.AFTERNOON)) {
            transactions = transactionRepository.findTransactionsByDoctorIdAndDateRange(
                    request.getDoctorId(),
                    today.atTime(12, 1),
                    today.atTime(20, 0)
            );
            if (transactions.isEmpty()) {
                throw new BadRequestException("No vaccine transactions found in the afternoon shift for doctor ID " + request.getDoctorId());
            }
        } else {
            transactions = transactionRepository.findTransactionsByDoctorIdAndDateRange(
                    request.getDoctorId(),
                    today.atTime(8, 0),
                    today.atTime(12, 0)
            );
            if (transactions.isEmpty()) {
                throw new BadRequestException("No vaccine transactions found in the morning shift for doctor ID " + request.getDoctorId());
            }
        }
        List<BatchTransaction> batchTransactions = transactions.stream().flatMap(t -> t.getBatchTransactions().stream()).toList();
        List<BatchTransaction> modifiedBTs = new ArrayList<>();
        List<Batch> modifiedBatches = new ArrayList<>();
        for (VaccineReturnRequest.VaccinesQuantity vq : request.getReturned()) {
            Vaccine vaccine = vaccineRepository.findById(vq.getVaccineId()).orElseThrow(
                    () -> new BadRequestException("Vaccine with ID " + vq.getVaccineId() + " not found")
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

                // Ensure we don't exceed remaining
                int addBack = Math.min(
                        Math.min(quantityToReturn, remaining),
                        batch.getBatchSize() - batch.getQuantity()
                );
                bt.setRemaining(bt.getRemaining() - addBack);
                batch.setQuantity(batch.getQuantity() + addBack);
                modifiedBTs.add(bt);
                modifiedBatches.add(batch);
                quantityToReturn -= addBack;
            }

            // This ensures the doctor is not returning more than they initially took.
            if (quantityToReturn > 0) {
                throw new BadRequestException("Return quantity exceeds what was taken for vaccine ID " + vq.getVaccineId());
            }
        }

        // Save each batch and transaction individually if saveAll is not available
        for (Batch batch : modifiedBatches) {
            batchRepository.save(batch);
        }

        for (BatchTransaction bt : modifiedBTs) {
            batchTransactionRepository.save(bt);
        }
    }
}