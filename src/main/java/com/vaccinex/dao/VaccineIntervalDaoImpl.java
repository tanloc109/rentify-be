package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineInterval;
import com.vaccinex.pojo.composite.VaccineIntervalId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Implementation of the VaccineIntervalDao interface
 */
@ApplicationScoped
public class VaccineIntervalDaoImpl extends AbstractDao<VaccineInterval, VaccineIntervalId> implements VaccineIntervalDao {

    public VaccineIntervalDaoImpl() {
        super(VaccineInterval.class);
    }

    @Override
    public List<VaccineInterval> findByToVaccine(Vaccine toVaccine) {
        TypedQuery<VaccineInterval> query = entityManager.createQuery(
                "SELECT vi FROM VaccineInterval vi WHERE vi.toVaccine = :toVaccine", VaccineInterval.class);
        query.setParameter("toVaccine", toVaccine);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteByFromVaccineId(int fromVaccineId) {
        Query query = entityManager.createQuery(
                "DELETE FROM VaccineInterval vi WHERE vi.fromVaccine.id = :fromVaccineId");
        query.setParameter("fromVaccineId", fromVaccineId);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public VaccineInterval save(VaccineInterval vaccineInterval) {
        if (entityManager.find(VaccineInterval.class, vaccineInterval.getId()) == null) {
            entityManager.persist(vaccineInterval);
            return vaccineInterval;
        } else {
            return entityManager.merge(vaccineInterval);
        }
    }
}