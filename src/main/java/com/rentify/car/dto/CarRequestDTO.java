package com.rentify.car.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarRequestDTO {
    String name;
    String description;
    String image;
    String plateLicense;
    int yearOfManufacture;
    int seat;
    BigDecimal pricePerDay;
    BigDecimal deposit;
    String location;
    Long ownerId;
    Long brandId;
    Long carTypeId;
}
