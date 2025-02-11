package com.rentify.transaction.dto;

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
    String type;
    String status;
    LocalDateTime createdAt;
    Timestamp version;
}
