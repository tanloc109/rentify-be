package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.VaccineUse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Implementation of the VaccineUseDao interface
 */
@ApplicationScoped
public class VaccineUseDaoImpl extends AbstractDao<VaccineUse, Integer> implements VaccineUseDao {

    public VaccineUseDaoImpl() {
        super(VaccineUse.class);
    }

    @Override
    public List<VaccineUse> findAllByDeletedFalse(int page, int size) {
        TypedQuery<VaccineUse> query = entityManager.createQuery(
                "SELECT vu FROM VaccineUse vu WHERE vu.deleted = false", 
                VaccineUse.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public long countByDeletedIsFalse() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(vu) FROM VaccineUse vu WHERE vu.deleted = false", 
                Long.class);
        return query.getSingleResult();
    }

    @Override
    public VaccineUse findPurposeById(int id) {
        return entityManager.find(VaccineUse.class, id);
    }

    @Override
    public VaccineUse findPurposeByName(String name) {
        TypedQuery<VaccineUse> query = entityManager.createQuery(
                "SELECT vu FROM VaccineUse vu WHERE vu.name = :name", 
                VaccineUse.class);
        query.setParameter("name", name);
        
        List<VaccineUse> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<VaccineUse> findByDeletedIsFalse() {
        return entityManager.createQuery(
                "SELECT vu FROM VaccineUse vu WHERE vu.deleted = false", 
                VaccineUse.class)
                .getResultList();
    }

    @Override
    public List<VaccineUse> findAllByIdInAndDeletedFalse(List<Integer> vaccineUseIds) {
        TypedQuery<VaccineUse> query = entityManager.createQuery(
                "SELECT vu FROM VaccineUse vu WHERE vu.id IN :ids AND vu.deleted = false", 
                VaccineUse.class);
        query.setParameter("ids", vaccineUseIds);
        return query.getResultList();
    }

    @Override
    public VaccineUse findByName(String name) {
        TypedQuery<VaccineUse> query = entityManager.createQuery(
                "SELECT vu FROM VaccineUse vu WHERE vu.name = :name",
                VaccineUse.class);
        query.setParameter("name", name);
        List<VaccineUse> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public VaccineUse save(VaccineUse vaccineUse) {
        if (vaccineUse.getId() == null) {
            entityManager.persist(vaccineUse);
            return vaccineUse;
        } else {
            return entityManager.merge(vaccineUse);
        }
    }
}