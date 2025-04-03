package com.vaccinex.service;

import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.dao.*;
import com.vaccinex.dto.paging.PagingRequest;
import com.vaccinex.dto.paging.TransactionPagingResponse;
import com.vaccinex.dto.request.TransactionCreateRequest;
import com.vaccinex.dto.request.TransactionUpdateRequest;
import com.vaccinex.pojo.*;
import com.vaccinex.pojo.composite.BatchTransactionId;
import com.vaccinex.pojo.enums.TransactionType;
import com.vaccinex.utils.PaginationUtil;
import jakarta.ejb.Stateless;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Stateless
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionDao transactionRepository;
    private final UserDao userRepository;
    private final BatchDao batchRepository;
    private final VaccineDao vaccineRepository;
    private final BatchTransactionDao batchTransactionRepository;

    @Override
    public MappingJacksonValue getAllTransactions(PagingRequest request) {
        PaginationUtil.Pageable pageable = PaginationUtil.getPageable(request);
        Map<String, String> maps = request.getFilters();
        maps.remove("pageNo");
        maps.remove("pageSize");
        maps.remove("params");
        maps.remove("sortBy");
        Specification<Transaction> specification = TransactionPagingResponse.filterByFields(maps);
        Page<Transaction> transactions = transactionRepository.findAll(specification, pageable);
        List<TransactionPagingResponse> mappedDTOs = transactions.getContent().stream().map(
                TransactionPagingResponse::fromEntity
        ).toList();
        return PaginationUtil.getPagedMappingJacksonValue(request, transactions, mappedDTOs, "Lấy tất cả giao dịch");
    }

    @Override
    public void createTransaction(TransactionCreateRequest request) {
        User doctor = userRepository.findByIdAndDeletedIsFalse(request.doctorId()).orElseThrow(
                () -> new BadRequestException("Không tìm thấy bác sĩ với ID: " + request.doctorId())
        );
        Vaccine vaccine = vaccineRepository.findById(request.vaccineId()).orElseThrow(
                () -> new BadRequestException("Không tìm thấy vaccine với ID: " + request.vaccineId())
        );
        List<Batch> batches = batchRepository.findByVaccineOrderByExpirationAsc(vaccine);
        int totalAmountInBatches = batches.stream().mapToInt(Batch::getQuantity).sum();
        if (request.quantityTaken() > totalAmountInBatches) {
            throw new BadRequestException("Số lượng vượt quá tổng số lượng vaccine!");
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
                () -> new BadRequestException("Không tìm thấy giao dịch với ID: " + transactionId)
        );
        User doctor = userRepository.findByIdAndDeletedIsFalse(request.doctorId()).orElseThrow(
                () -> new BadRequestException("Không tìm thấy bác sĩ với ID: " + request.doctorId())
        );
        transaction.setDate(request.date());
        transaction.setDoctor(doctor);
        transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(Integer id) {
        Transaction transaction = transactionRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                () -> new BadRequestException("Không tìm thấy giao dịch với ID: " + id)
        );
        transaction.setDeleted(true);
        transactionRepository.save(transaction);
    }
}
