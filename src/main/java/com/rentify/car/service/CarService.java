package com.rentify.car.service;

import com.rentify.base.exception.BadRequestException;
import com.rentify.base.exception.IdNotFoundException;
import com.rentify.brand.dao.BrandDAO;
import com.rentify.brand.entity.Brand;
import com.rentify.car.dao.CarDAO;
import com.rentify.car.dto.CarDTO;
import com.rentify.car.dto.CarRequestDTO;
import com.rentify.car.entity.Car;
import com.rentify.car.entity.CarStatus;
import com.rentify.car.rest.mapper.CarMapper;
import com.rentify.type.dao.TypeDAO;
import com.rentify.user.dao.UserDAO;
import com.rentify.user.service.UserService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class CarService {

    @Inject
    private CarDAO carDAO;

    @Inject
    private CarMapper carMapper;

    @Inject
    private UserDAO userDAO;

    @Inject
    private BrandDAO brandDAO;

    @Inject
    private TypeDAO typeDAO;

    public List<CarDTO> findAll() {
        return carMapper.toDTOs(carDAO.findAll().stream()
                .filter(car -> car.getDeletedAt() == null)
                .collect(Collectors.toList()));
    }

    public CarDTO findById(Long carId) {
        Car car = carDAO.findById(carId).orElseThrow(() -> new IdNotFoundException("Cannot found car with id: " + carId));
        if (car.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Car with id %s is deleted before", carId));
        }
        return carMapper.toDTO(car);
    }

    public CarDTO createCar(CarRequestDTO carRequestDTO) {
        Car car = carMapper.toEntity(carRequestDTO);
        car.setCarStatus(CarStatus.AVAILABLE);
        car.setOwner(userDAO.findById(carRequestDTO.getOwnerId()).orElseThrow(() -> new IdNotFoundException("Cannot found owner with id:" + carRequestDTO.getOwnerId())));
        car.setBrand(brandDAO.findById(carRequestDTO.getBrandId()).orElseThrow(() -> new IdNotFoundException("Cannot found brand with id:" + carRequestDTO.getBrandId())));
        car.setCarType(typeDAO.findById(carRequestDTO.getCarTypeId()).orElseThrow(() -> new IdNotFoundException("Cannot found type with id:" + carRequestDTO.getCarTypeId())));
        carDAO.save(car);
        return carMapper.toDTO(car);
    }

    public CarDTO updateCar(Long carId, CarRequestDTO carUpdateDTO) {
        Car updateCar = carDAO.findById(carId).orElseThrow(() -> new IdNotFoundException("Cannot found car with id: " + carId));
        if (updateCar.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Car with id %s is deleted before", carId));
        }
        Car car = carMapper.toEntity(carUpdateDTO);
        car.setId(carId);
        return carMapper.toDTO(carDAO.update(updateCar));
    }

    public void deleteCar(Long carId) {
        Car deleteCar = carDAO.findById(carId).orElseThrow(() -> new IdNotFoundException("Cannot found car with id: " + carId));
        if (deleteCar.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Car with id %s is deleted before", carId));
        }
        deleteCar.setDeletedAt(LocalDateTime.now());
        carDAO.update(deleteCar);
    }

}
