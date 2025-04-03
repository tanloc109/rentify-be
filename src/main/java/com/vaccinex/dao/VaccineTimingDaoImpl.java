package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineTiming;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the VaccineTimingDao interface
 */
@ApplicationScoped
public class VaccineTimingDaoImpl extends AbstractDao<VaccineTiming, Integer> implements VaccineTimingDao {

    public VaccineTimingDaoImpl() {
        super(VaccineTiming.class);
    }

    @Override
    public Optional<VaccineTiming> findByIdAndDeletedIsFalse(Integer id) {
        TypedQuery<VaccineTiming> query = entityManager.createQuery(
                "SELECT vt FROM VaccineTiming vt WHERE vt.id = :id AND vt.deleted = false", 
                VaccineTiming.class);
        query.setParameter("id", id);
        
        List<VaccineTiming> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<VaccineTiming> findByVaccineAndDoseNoAndDeletedIsFalse(Vaccine vaccine, int doseNo) {
        TypedQuery<VaccineTiming> query = entityManager.createQuery(
                "SELECT vt FROM VaccineTiming vt WHERE vt.vaccine = :vaccine AND vt.doseNo = :doseNo AND vt.deleted = false", 
                VaccineTiming.class);
        query.setParameter("vaccine", vaccine);
        query.setParameter("doseNo", doseNo);
        
        List<VaccineTiming> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    @Transactional
    public VaccineTiming save(VaccineTiming vaccineTiming) {
        if (vaccineTiming.getId() == null) {
            entityManager.persist(vaccineTiming);
            return vaccineTiming;
        } else {
            return entityManager.merge(vaccineTiming);
        }
    }
}