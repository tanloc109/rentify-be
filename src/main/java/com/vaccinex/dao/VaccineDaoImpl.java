package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Vaccine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the VaccineDao interface
 */
@ApplicationScoped
public class VaccineDaoImpl extends AbstractDao<Vaccine, Integer> implements VaccineDao {

    public VaccineDaoImpl() {
        super(Vaccine.class);
    }

    @Override
    public List<Vaccine> findAllByDeletedFalse(int page, int size) {
        TypedQuery<Vaccine> query = entityManager.createQuery(
                "SELECT v FROM Vaccine v WHERE v.deleted = false", Vaccine.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long countByDeletedIsFalse() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(v) FROM Vaccine v WHERE v.deleted = false", Long.class);
        return query.getSingleResult();
    }

    @Override
    public Vaccine findVaccineByVaccineCode(String vaccineCode) {
        TypedQuery<Vaccine> query = entityManager.createQuery(
                "SELECT v FROM Vaccine v WHERE v.vaccineCode = :vaccineCode", Vaccine.class);
        query.setParameter("vaccineCode", vaccineCode);

        List<Vaccine> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public Vaccine findVaccineById(int id) {
        return entityManager.find(Vaccine.class, id);
    }

    @Override
    public Optional<Vaccine> findByIdAndDeletedIsFalse(Integer id) {
        TypedQuery<Vaccine> query = entityManager.createQuery(
                "SELECT v FROM Vaccine v WHERE v.id = :id AND v.deleted = false", Vaccine.class);
        query.setParameter("id", id);

        List<Vaccine> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Double getMaxPrice() {
        TypedQuery<Double> query = entityManager.createQuery(
                "SELECT MAX(v.price) FROM Vaccine v", Double.class);
        return query.getSingleResult();
    }

    @Override
    public Integer getMaxAge() {
        TypedQuery<Integer> query = entityManager.createQuery(
                "SELECT MAX(v.maxAge) FROM Vaccine v", Integer.class);
        return query.getSingleResult();
    }

    @Override
    public List<Vaccine> findByDeletedIsFalse() {
        return entityManager.createQuery(
                "SELECT v FROM Vaccine v WHERE v.deleted = false", Vaccine.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Vaccine save(Vaccine vaccine) {
        if (vaccine.getId() == null) {
            entityManager.persist(vaccine);
            return vaccine;
        } else {
            return entityManager.merge(vaccine);
        }
    }
}