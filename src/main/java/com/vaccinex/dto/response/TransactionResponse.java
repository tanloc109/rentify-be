package com.vaccinex.dto.response;

import com.vaccinex.pojo.Transaction;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionResponse {
    Integer id;
    LocalDateTime date;
    Integer doctorId;
    String doctorName;
    List<BatchInfo> batches;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BatchInfo {
        Integer batchId;
        Integer quantityTaken;
        Integer remaining;
    }

    public static TransactionResponse fromEntity(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .date(transaction.getDate())
                .doctorId(transaction.getDoctor().getId())
                .doctorName(transaction.getDoctor().getFullName())
                .batches(transaction.getBatchTransactions().stream().map(
                        bt -> BatchInfo.builder()
                                .batchId(bt.getBatch().getId())
                                .quantityTaken(bt.getQuantityTaken())
                                .remaining(bt.getRemaining())
                                .build()
                ).toList())
                .build();
    }
}
