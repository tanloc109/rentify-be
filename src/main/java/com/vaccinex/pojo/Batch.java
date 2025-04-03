package com.vaccinex.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Batch extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(unique = true, columnDefinition = "NVARCHAR(255)")
    String batchCode;

    Integer batchSize;

    Integer quantity;

    @Column(columnDefinition = "DATETIME2(0)")
    LocalDateTime manufactured;

    @Column(columnDefinition = "DATETIME2(0)")
    LocalDateTime imported;

    @Column(columnDefinition = "DATETIME2(0)")
    LocalDateTime expiration;

    @Column(columnDefinition = "NVARCHAR(255)")
    String distributer;

    @ManyToOne
    @JsonManagedReference
    Vaccine vaccine;

    @OneToMany(mappedBy = "batch")
    List<BatchTransaction> batchTransactions;

    @OneToMany(mappedBy = "batch")
    @JsonBackReference
    List<VaccineSchedule> vaccineSchedules;
}
