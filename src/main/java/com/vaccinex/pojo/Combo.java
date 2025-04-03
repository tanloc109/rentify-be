package com.vaccinex.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Combo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name", nullable = false, unique = true, columnDefinition = "NVARCHAR(255)")
    String name;

    @Column(columnDefinition = "NVARCHAR(1000)")
    String description;

    Double price;

    Integer minAge;

    Integer maxAge;

    @OneToMany(mappedBy = "combo")
    @JsonBackReference
    List<VaccineSchedule> vaccineSchedules;

    @OneToMany(mappedBy = "combo", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonBackReference
    List<VaccineCombo> vaccineCombos;

    @ManyToMany
    List<Order> orders;
}
