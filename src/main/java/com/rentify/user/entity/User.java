package com.rentify.user.entity;

import com.rentify.base.entity.BaseEntity;
import com.rentify.car.entity.Car;
import com.rentify.feedback.entity.Feedback;
import com.rentify.rental.entity.Rental;
import com.rentify.wallet.entity.Wallet;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = true)
@Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 10)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Wallet wallet;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Car> cars;

    @OneToMany(mappedBy = "renter", cascade = CascadeType.ALL)
    private List<Rental> rentals;

    @OneToMany(mappedBy = "renter", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;
}
