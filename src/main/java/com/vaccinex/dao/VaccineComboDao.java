package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Combo;
import com.vaccinex.pojo.VaccineCombo;
import com.vaccinex.pojo.composite.VaccineComboId;
import java.util.List;

/**
 * VaccineCombo Data Access Object interface
 */
public interface VaccineComboDao extends GenericDao<VaccineCombo, VaccineComboId> {
    List<VaccineCombo> getVaccineCombosByCombo(Combo combo);
}

