package com.vaccinex.thirdparty.refund;

import com.vaccinex.pojo.Payment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "refund_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime requestDate;

    @Column
    private String responseCode;

    @Column
    private String responseMessage;

    @Column
    private String transactionNo;

    @Column
    private String transactionStatus;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private boolean success;
}