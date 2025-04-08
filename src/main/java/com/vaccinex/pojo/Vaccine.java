package com.vaccinex.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Vaccine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    @Column(columnDefinition = "VARCHAR(1000)")
    String description;

    @Column(name = "vaccine_code", nullable = false, unique = true, columnDefinition = "VARCHAR(255)")
    String vaccineCode;

    @Column(columnDefinition = "VARCHAR(255)")
    String manufacturer;

    Double price;

    Long expiresInDays;

    Integer minAge;

    Integer maxAge;

    Integer dose;

    boolean activated;

    @OneToMany(mappedBy = "toVaccine", cascade = CascadeType.ALL)
    @JsonBackReference
    List<VaccineInterval> toVaccineIntervals;

    @OneToMany(mappedBy = "fromVaccine", cascade = CascadeType.ALL)
    @JsonBackReference
    List<VaccineInterval> fromVaccineIntervals;

    @ManyToMany
    @JsonBackReference
    List<VaccineUse> uses;

    @OneToMany(mappedBy = "vaccine")
    @JsonBackReference
    List<VaccineSchedule> vaccineSchedules;

    @OneToMany(mappedBy = "vaccine", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonBackReference
    List<VaccineCombo> vaccineCombos;

    @OneToMany(mappedBy = "vaccine", cascade = CascadeType.ALL)
    List<VaccineTiming> vaccineTimings;

    @OneToMany(mappedBy = "vaccine")
    @JsonBackReference
    List<Batch> batches;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vaccine vaccine = (Vaccine) obj;
        return Objects.equals(id, vaccine.id);
    }
}
