package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Child;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the ChildrenDao interface
 */
@ApplicationScoped
public class ChildrenDaoImpl extends AbstractDao<Child, Integer> implements ChildrenDao {

    public ChildrenDaoImpl() {
        super(Child.class);
    }

    @Override
    public Optional<Child> findByIdAndDeletedIsFalse(Integer id) {
        TypedQuery<Child> query = entityManager.createQuery(
                "SELECT c FROM Child c WHERE c.id = :id AND c.deleted = false", Child.class);
        query.setParameter("id", id);
        
        List<Child> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Child> findAllByDeletedIsFalse() {
        return entityManager.createQuery(
                "SELECT c FROM Child c WHERE c.deleted = false", Child.class)
                .getResultList();
    }

    @Override
    public List<Child> findAllByGuardianIdAndDeletedIsFalse(Integer guardianId) {
        TypedQuery<Child> query = entityManager.createQuery(
                "SELECT c FROM Child c WHERE c.guardian.id = :guardianId AND c.deleted = false", Child.class);
        query.setParameter("guardianId", guardianId);
        return query.getResultList();
    }

    @Override
    @Transactional
    public Child save(Child child) {
        if (child.getId() == null) {
            entityManager.persist(child);
            return child;
        } else {
            return entityManager.merge(child);
        }
    }
}