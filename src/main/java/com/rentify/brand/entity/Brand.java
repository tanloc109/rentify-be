package com.rentify.brand.entity;

import com.rentify.base.entity.BaseEntity;
import com.rentify.car.entity.Car;
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
@Table(name = "brands")
public class Brand extends BaseEntity {
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String country;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<Car> cars;
}
