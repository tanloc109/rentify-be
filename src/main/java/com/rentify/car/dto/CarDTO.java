package com.rentify.car.dto;

import com.rentify.brand.dto.BrandDTO;
import com.rentify.car.entity.CarStatus;
import com.rentify.type.dto.TypeDTO;
import com.rentify.user.dto.UserDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CarDTO {
    Long id;
    String name;
    String description;
    String image;
    String plateLicense;
    int yearOfManufacture;
    int seat;
    BigDecimal pricePerDay;
    BigDecimal deposit;
    String location;
    CarStatus carStatus;
    UserDTO owner;
    BrandDTO brand;
    TypeDTO carType;
    Timestamp version;
}
