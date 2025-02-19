package com.rentify.car.entity;

import com.rentify.base.entity.BaseEntity;
import com.rentify.brand.entity.Brand;
import com.rentify.type.entity.Type;
import com.rentify.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "cars")
public class Car extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    private String plateLicense;

    private int yearOfManufacture;

    private int seat;

    @Column(nullable = false)
    private BigDecimal pricePerDay;

    private BigDecimal deposit;

    String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    CarStatus carStatus = CarStatus.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private Type carType;
}
