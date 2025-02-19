package com.rentify.car.rest.mapper;

import com.rentify.car.dto.CarDTO;
import com.rentify.car.dto.CarRequestDTO;
import com.rentify.car.entity.Car;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface CarMapper {
    CarDTO toDTO(Car car);
    Car toEntity(CarRequestDTO carRequestDTO);
    List<CarDTO> toDTOs(List<Car> cars);
}
