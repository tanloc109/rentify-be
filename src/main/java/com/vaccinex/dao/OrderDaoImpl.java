package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Implementation of the OrderDao interface
 */
@ApplicationScoped
public class OrderDaoImpl extends AbstractDao<Order, Integer> implements OrderDao {

    public OrderDaoImpl() {
        super(Order.class);
    }

    @Override
    public List<Order> findByCustomerId(Integer customerId) {
        TypedQuery<Order> query = entityManager.createQuery(
                "SELECT o FROM Order o WHERE o.customer.id = :customerId", Order.class);
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    @Override
    @Transactional
    public Order save(Order order) {
        if (order.getId() == null) {
            entityManager.persist(order);
            return order;
        } else {
            return entityManager.merge(order);
        }
    }
}