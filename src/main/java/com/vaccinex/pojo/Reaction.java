package com.vaccinex.pojo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Reaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    LocalDateTime date;

    @Column(columnDefinition = "NVARCHAR(255)")
    String reaction;

    @Column(columnDefinition = "NVARCHAR(255)")
    String reportedBy;

    @ManyToOne
    @JsonManagedReference
    VaccineSchedule schedule;

}
