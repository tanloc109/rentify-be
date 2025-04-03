package com.vaccinex.pojo;

import com.vaccinex.pojo.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    LocalDateTime date;

    Double amount;

    @Column(name = "vnp_txn_ref")
    String vnpTxnRef;

    @Column(name = "vnp_transaction_no")
    String vnpTransactionNo;

    @ManyToOne
    User customer;

    @ManyToOne
    Order order;
}
