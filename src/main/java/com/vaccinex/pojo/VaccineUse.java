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
public class VaccineUse extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "name", nullable = false, unique = true, columnDefinition = "NVARCHAR(255)")
    String name;

    @Column(name = "description", columnDefinition = "NVARCHAR(1000)")
    String description;

    @ManyToMany(mappedBy = "uses")
    @JsonBackReference
    List<Vaccine> vaccines;

}
