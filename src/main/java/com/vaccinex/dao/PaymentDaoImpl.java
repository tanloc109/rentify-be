package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Payment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

/**
 * Implementation of the PaymentDao interface
 */
@ApplicationScoped
public class PaymentDaoImpl extends AbstractDao<Payment, Integer> implements PaymentDao {

    public PaymentDaoImpl() {
        super(Payment.class);
    }

    @Override
    @Transactional
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            entityManager.persist(payment);
            return payment;
        } else {
            return entityManager.merge(payment);
        }
    }
}