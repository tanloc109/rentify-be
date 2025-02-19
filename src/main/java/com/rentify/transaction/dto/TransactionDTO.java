package com.rentify.transaction.dto;

import com.rentify.transaction.entity.TransactionStatus;
import com.rentify.transaction.entity.TransactionType;
import com.rentify.wallet.entity.Wallet;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionDTO {
    Long id;
    BigDecimal amount;
    BigDecimal balance;
    TransactionType type;
    TransactionStatus status;
    Wallet wallet;
    Timestamp version;
}
