package com.vaccinex.pojo;

import com.vaccinex.pojo.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "vaccine_transaction")
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "DATETIME2(0)")
    LocalDateTime date;

    @Enumerated(EnumType.STRING)
    TransactionType type;

    @OneToMany(mappedBy = "transaction")
    List<BatchTransaction> batchTransactions;

    @ManyToOne
    User doctor;
}
