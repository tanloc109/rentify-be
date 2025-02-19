package com.rentify.car.dao;

import com.rentify.base.dao.BaseDAO;
import com.rentify.car.entity.Car;
import jakarta.ejb.Stateless;

@Stateless
public class CarDAO extends BaseDAO<Car> {

    public CarDAO() {
        super(Car.class);
    }

}
