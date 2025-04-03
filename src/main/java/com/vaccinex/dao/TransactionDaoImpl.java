package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transaction;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the TransactionDao interface
 */
@ApplicationScoped
public class TransactionDaoImpl extends AbstractDao<Transaction, Integer> implements TransactionDao {

    public TransactionDaoImpl() {
        super(Transaction.class);
    }

    @Override
    public Optional<Transaction> findByIdAndDeletedIsFalse(Integer id) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.id = :id AND t.deleted = false", Transaction.class);
        query.setParameter("id", id);

        List<Transaction> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Transaction> findByDoctorId(Integer doctorId) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.doctor.id = :doctorId", Transaction.class);
        query.setParameter("doctorId", doctorId);
        return query.getResultList();
    }

    @Override
    public List<Transaction> findByDoctorIdAndDateAfterAndDateBefore(Integer doctorId, LocalDateTime dateAfter, LocalDateTime dateBefore) {
        TypedQuery<Transaction> query = entityManager.createQuery(
                "SELECT t FROM Transaction t WHERE t.doctor.id = :doctorId AND t.date > :dateAfter AND t.date < :dateBefore",
                Transaction.class);
        query.setParameter("doctorId", doctorId);
        query.setParameter("dateAfter", dateAfter);
        query.setParameter("dateBefore", dateBefore);
        return query.getResultList();
    }

    @Override
    @Transactional
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null) {
            entityManager.persist(transaction);
            return transaction;
        } else {
            return entityManager.merge(transaction);
        }
    }
}