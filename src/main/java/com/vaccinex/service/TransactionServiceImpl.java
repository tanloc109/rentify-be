package com.vaccinex.service;

import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.dao.*;
import com.vaccinex.dto.response.TransactionResponse;
import com.vaccinex.dto.request.TransactionCreateRequest;
import com.vaccinex.dto.request.TransactionUpdateRequest;
import com.vaccinex.pojo.*;
import com.vaccinex.pojo.composite.BatchTransactionId;
import com.vaccinex.pojo.enums.TransactionType;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class TransactionServiceImpl implements TransactionService {


    @Inject
    private TransactionDao transactionRepository;

    @Inject
    private UserDao userRepository;

    @Inject
    private BatchDao batchRepository;

    @Inject
    private VaccineDao vaccineRepository;

    @Inject
    private BatchTransactionDao batchTransactionRepository;

    @Override
    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(TransactionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void createTransaction(TransactionCreateRequest request) {
        User doctor = userRepository.findByIdAndDeletedIsFalse(request.doctorId()).orElseThrow(
                () -> new BadRequestException("Doctor not found with ID: " + request.doctorId())
        );
        Vaccine vaccine = vaccineRepository.findById(request.vaccineId()).orElseThrow(
                () -> new BadRequestException("Vaccine not found with ID: " + request.vaccineId())
        );
        List<Batch> batches = batchRepository.findByVaccineOrderByExpirationAsc(vaccine);
        int totalAmountInBatches = batches.stream().mapToInt(Batch::getQuantity).sum();
        if (request.quantityTaken() > totalAmountInBatches) {
            throw new BadRequestException("Quantity exceeds total available vaccine amount!");
        }
        Transaction transaction = Transaction.builder()
                .date(request.date())
                .type(TransactionType.EXPORT)
                .doctor(doctor)
                .build();
        transaction = transactionRepository.save(transaction);
        int quantityNeedToTake = request.quantityTaken();
        int batchIndex = 0;
        while(quantityNeedToTake > 0) {
            Batch batch = batches.get(batchIndex);
            int taken = Math.min(quantityNeedToTake, batch.getQuantity());
            quantityNeedToTake -= taken;
            batch.setQuantity(batch.getQuantity() - taken);
            batch = batchRepository.save(batch);
            BatchTransaction batchTransaction = BatchTransaction.builder()
                    .id(BatchTransactionId.builder()
                            .batchId(batch.getId())
                            .transactionId(transaction.getId())
                            .build())
                    .transaction(transaction)
                    .batch(batch)
                    .quantityTaken(taken)
                    .remaining(taken)
                    .build();
            batchTransactionRepository.save(batchTransaction);
            batchIndex++;
        }
    }

    @Override
    public void updateTransaction(Integer transactionId, TransactionUpdateRequest request) {
        Transaction transaction = transactionRepository.findByIdAndDeletedIsFalse(transactionId).orElseThrow(
                () -> new BadRequestException("Transaction not found with ID: " + transactionId)
        );
        User doctor = userRepository.findByIdAndDeletedIsFalse(request.doctorId()).orElseThrow(
                () -> new BadRequestException("Doctor not found with ID: " + request.doctorId())
        );
        transaction.setDate(request.date());
        transaction.setDoctor(doctor);
        transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(Integer id) {
        Transaction transaction = transactionRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                () -> new BadRequestException("Transaction not found with ID: " + id)
        );
        transaction.setDeleted(true);
        transactionRepository.save(transaction);
    }
}