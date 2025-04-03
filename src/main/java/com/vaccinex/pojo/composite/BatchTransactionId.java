package com.vaccinex.pojo.composite;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
public class BatchTransactionId {

    @Column(name = "batch_id")
    private Integer batchId;

    @Column(name = "transaction_id")
    private Integer transactionId;

}
