package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Order;
import java.util.List;

/**
 * Order Data Access Object interface
 */
public interface OrderDao extends GenericDao<Order, Integer> {
    List<Order> findByCustomerId(Integer customerId);
}
