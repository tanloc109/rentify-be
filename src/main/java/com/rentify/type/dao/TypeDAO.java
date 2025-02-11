package com.rentify.type.dao;

import com.rentify.base.dao.BaseDAO;
import com.rentify.type.entity.Type;
import jakarta.ejb.Stateless;

@Stateless
public class TypeDAO extends BaseDAO<Type> {

    public TypeDAO() {
        super(Type.class);
    }

}
