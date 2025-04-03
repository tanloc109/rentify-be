package com.vaccinex.pojo;

import com.vaccinex.pojo.composite.BatchTransactionId;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchTransaction {

    @EmbeddedId
    BatchTransactionId id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    @MapsId("transactionId")
    Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    @MapsId("batchId")
    Batch batch;

    Integer quantityTaken;

    Integer remaining;
}
