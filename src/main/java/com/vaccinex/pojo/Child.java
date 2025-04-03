package com.vaccinex.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.vaccinex.pojo.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Child extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "NVARCHAR(255)")
    String firstName;

    @Column(columnDefinition = "NVARCHAR(255)")
    String lastName;

    LocalDate dob;

    @Enumerated(EnumType.STRING)
    Gender gender;

    Double weight;

    Double height;

    @Column(columnDefinition = "NVARCHAR(10)")
    String bloodType;

    @Column(columnDefinition = "NVARCHAR(1000)")
    String healthNote;

    @ManyToOne
    @JsonManagedReference
    User guardian;

    @OneToMany(mappedBy = "child")
    @JsonBackReference
    List<Order> orders;

    @OneToMany(mappedBy = "child")
    @JsonBackReference
    List<VaccineSchedule> schedules;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public int getAge() {
        if (dob == null) {
            return 0; // hoặc có thể throw exception nếu cần
        }
        return Period.between(dob, LocalDate.now()).getYears();
    }

    public int getAge(LocalDate date) {
        if (dob == null) {
            return 0;
        }
        return Period.between(dob, date).getYears();
    }
}
