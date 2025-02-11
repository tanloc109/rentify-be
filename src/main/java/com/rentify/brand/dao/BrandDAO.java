package com.rentify.brand.dao;

import com.rentify.base.dao.BaseDAO;
import com.rentify.brand.entity.Brand;
import jakarta.ejb.Stateless;

@Stateless
public class BrandDAO extends BaseDAO<Brand> {

    public BrandDAO() {
        super(Brand.class);
    }

}
