package com.vaccinex.thirdparty.refund;

import com.vaccinex.pojo.Payment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@ApplicationScoped
public class RefundTransactionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public RefundTransaction save(RefundTransaction refundTransaction) {
        if (refundTransaction.getId() == null) {
            entityManager.persist(refundTransaction);
            return refundTransaction;
        } else {
            return entityManager.merge(refundTransaction);
        }
    }

    public void delete(RefundTransaction refundTransaction) {
        entityManager.remove(entityManager.contains(refundTransaction) ?
                refundTransaction : entityManager.merge(refundTransaction));
    }

    public RefundTransaction findById(Long id) {
        return entityManager.find(RefundTransaction.class, id);
    }

    public List<RefundTransaction> findAll() {
        return entityManager.createQuery("SELECT r FROM RefundTransaction r", RefundTransaction.class)
                .getResultList();
    }

    public List<RefundTransaction> findByPayment(Payment payment) {
        TypedQuery<RefundTransaction> query = entityManager.createQuery(
                "SELECT r FROM RefundTransaction r WHERE r.payment = :payment",
                RefundTransaction.class);
        query.setParameter("payment", payment);
        return query.getResultList();
    }

    public List<RefundTransaction> findBySuccess(boolean success) {
        TypedQuery<RefundTransaction> query = entityManager.createQuery(
                "SELECT r FROM RefundTransaction r WHERE r.success = :success",
                RefundTransaction.class);
        query.setParameter("success", success);
        return query.getResultList();
    }
}