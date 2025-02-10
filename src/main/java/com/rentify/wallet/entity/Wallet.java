package com.rentify.wallet.entity;
import com.rentify.base.entity.BaseEntity;
import com.rentify.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "wallets")
public class Wallet extends BaseEntity {
    private BigDecimal balance = BigDecimal.valueOf(0);

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}