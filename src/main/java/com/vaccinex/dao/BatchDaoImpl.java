package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Batch;
import com.vaccinex.pojo.Vaccine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the BatchDao interface
 */
@ApplicationScoped
public class BatchDaoImpl extends AbstractDao<Batch, Integer> implements BatchDao {

    public BatchDaoImpl() {
        super(Batch.class);
    }

    @Override
    public List<Batch> findByDeletedIsFalse() {
        return entityManager.createQuery(
                "SELECT b FROM Batch b WHERE b.deleted = false", Batch.class)
                .getResultList();
    }

    @Override
    public Optional<Batch> findByIdAndDeletedIsFalse(Integer id) {
        TypedQuery<Batch> query = entityManager.createQuery(
                "SELECT b FROM Batch b WHERE b.id = :id AND b.deleted = false", Batch.class);
        query.setParameter("id", id);
        
        List<Batch> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Batch> findByVaccineOrderByExpirationAsc(Vaccine vaccine) {
        TypedQuery<Batch> query = entityManager.createQuery(
                "SELECT b FROM Batch b WHERE b.vaccine = :vaccine ORDER BY b.expiration ASC", Batch.class);
        query.setParameter("vaccine", vaccine);
        return query.getResultList();
    }

    @Override
    public List<Batch> findByVaccineIdAndExpirationBeforeAndDeletedIsFalse(Integer vaccineId, LocalDateTime date) {
        TypedQuery<Batch> query = entityManager.createQuery(
                "SELECT b FROM Batch b WHERE b.deleted = false AND b.vaccine.id = :vaccineId AND b.expiration >= :date", 
                Batch.class);
        query.setParameter("vaccineId", vaccineId);
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<Batch> findByVaccineIdAndExpirationAfter(Integer vaccineId, LocalDateTime appointmentDate) {
        TypedQuery<Batch> query = entityManager.createQuery(
                "SELECT b FROM Batch b WHERE b.vaccine.id = :vaccineId AND b.expiration > :appointmentDate AND b.quantity > 0", 
                Batch.class);
        query.setParameter("vaccineId", vaccineId);
        query.setParameter("appointmentDate", appointmentDate);
        return query.getResultList();
    }

    @Override
    @Transactional
    public Batch save(Batch batch) {
        if (batch.getId() == null) {
            entityManager.persist(batch);
            return batch;
        } else {
            return entityManager.merge(batch);
        }
    }

    @Override
    public List<Batch> findByVaccineIdAndExpirationBeforeAndDeletedIsFalseOrderByExpirationAsc(Integer vaccineId, LocalDateTime date) {
        TypedQuery<Batch> query = entityManager.createQuery(
                "SELECT b FROM Batch b WHERE b.deleted = false " +
                        "AND b.vaccine.id = :vaccineId " +
                        "AND b.expiration <= :date " +
                        "ORDER BY b.expiration ASC",
                Batch.class);
        query.setParameter("vaccineId", vaccineId);
        query.setParameter("date", date);
        return query.getResultList();
    }
}