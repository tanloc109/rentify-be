package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Combo;
import com.vaccinex.pojo.VaccineCombo;
import com.vaccinex.pojo.composite.VaccineComboId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Implementation of the VaccineComboDao interface
 */
@ApplicationScoped
public class VaccineComboDaoImpl extends AbstractDao<VaccineCombo, VaccineComboId> implements VaccineComboDao {

    public VaccineComboDaoImpl() {
        super(VaccineCombo.class);
    }

    @Override
    public List<VaccineCombo> getVaccineCombosByCombo(Combo combo) {
        TypedQuery<VaccineCombo> query = entityManager.createQuery(
                "SELECT vc FROM VaccineCombo vc WHERE vc.combo = :combo", VaccineCombo.class);
        query.setParameter("combo", combo);
        return query.getResultList();
    }

    @Override
    @Transactional
    public VaccineCombo save(VaccineCombo vaccineCombo) {
        if (entityManager.find(VaccineCombo.class, vaccineCombo.getId()) == null) {
            entityManager.persist(vaccineCombo);
            return vaccineCombo;
        } else {
            return entityManager.merge(vaccineCombo);
        }
    }
}