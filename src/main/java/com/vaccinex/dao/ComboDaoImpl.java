package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Combo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the ComboDao interface
 */
@ApplicationScoped
public class ComboDaoImpl extends AbstractDao<Combo, Integer> implements ComboDao {

    public ComboDaoImpl() {
        super(Combo.class);
    }

    @Override
    public List<Combo> findAllByDeletedFalse(int page, int size) {
        TypedQuery<Combo> query = entityManager.createQuery(
                "SELECT c FROM Combo c WHERE c.deleted = false", Combo.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public List<Combo> findByDeletedIsFalse(int page, int size) {
        return findAllByDeletedFalse(page, size);
    }

    @Override
    public Optional<Combo> findByIdAndDeletedIsFalse(Integer id) {
        TypedQuery<Combo> query = entityManager.createQuery(
                "SELECT c FROM Combo c WHERE c.id = :id AND c.deleted = false", Combo.class);
        query.setParameter("id", id);
        
        List<Combo> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Combo findComboByName(String name) {
        TypedQuery<Combo> query = entityManager.createQuery(
                "SELECT c FROM Combo c WHERE c.name = :name", Combo.class);
        query.setParameter("name", name);
        
        List<Combo> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public Combo findComboById(int id) {
        return entityManager.find(Combo.class, id);
    }

    @Override
    public Double getMaxPrice() {
        TypedQuery<Double> query = entityManager.createQuery(
                "SELECT MAX(c.price) FROM Combo c", Double.class);
        return query.getSingleResult();
    }

    @Override
    public Integer getMaxAge() {
        TypedQuery<Integer> query = entityManager.createQuery(
                "SELECT MAX(c.maxAge) FROM Combo c", Integer.class);
        return query.getSingleResult();
    }

    @Override
    public long countByDeletedIsFalse() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(c) FROM Combo c WHERE c.deleted = false", Long.class);
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public Combo save(Combo combo) {
        if (combo.getId() == null) {
            entityManager.persist(combo);
            return combo;
        } else {
            return entityManager.merge(combo);
        }
    }
}