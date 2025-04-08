package com.vaccinex.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class VaccineSchedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    LocalDateTime date;

    @Enumerated(EnumType.STRING)
    VaccineScheduleStatus status;

    @Min(1)
    @Max(5)
    Integer feedback;

    Integer orderNo;

    @Column(columnDefinition = "VARCHAR(1000)")
    String notes;

    @ManyToOne
    @JsonManagedReference
    Vaccine vaccine;

    @ManyToOne
    @JsonManagedReference
    User doctor;

    @ManyToOne
    @JsonManagedReference
    User customer;

    @ManyToOne
    @JsonManagedReference
    Order order;

    @OneToMany(mappedBy = "schedule")
    @JsonBackReference
    @ToString.Exclude
    List<Notification> notifications;

    @OneToMany(mappedBy = "schedule")
    @JsonBackReference
    @ToString.Exclude
    List<Reaction> reactions;

    @ManyToOne
    @JsonManagedReference
    Combo combo;

    @ManyToOne
    @JsonManagedReference
    Batch batch;

    @ManyToOne
    @JsonManagedReference
    Child child;
}
