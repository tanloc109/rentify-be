package com.vaccinex.pojo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "user_account")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(columnDefinition = "VARCHAR(255)")
    String firstName;

    @Column(columnDefinition = "VARCHAR(255)")
    String lastName;

    @Column(unique = true, nullable = false)
    String email;

    @Column(unique = true, nullable = false)
    String phone;

    String password;

    @Builder.Default
    boolean enabled = true;

    @Column(columnDefinition = "VARCHAR(1000)")
    String address;

    LocalDate dob;

    Integer age;

    @Builder.Default
    boolean nonLocked = true;

    @Column(length = 2048)
    String accessToken;

    @Column(length = 2048)
    String refreshToken;

    @ManyToOne
    Role role;

    @OneToMany(mappedBy = "customer")
    List<Payment> payments;

    @OneToMany(mappedBy = "doctor")
    @JsonBackReference
    List<VaccineSchedule> doctorSchedules;

    @OneToMany(mappedBy = "doctor")
    List<Transaction> doctorTransactions;

    @OneToMany(mappedBy = "customer")
    @JsonBackReference
    List<VaccineSchedule> customerSchedules;

    @OneToMany(mappedBy = "guardian")
    List<Child> children;

    @OneToMany(mappedBy = "customer")
    List<Order> orders;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
