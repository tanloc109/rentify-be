package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineInterval;
import com.vaccinex.pojo.composite.VaccineIntervalId;

import java.util.List;

/**
 * VaccineInterval Data Access Object interface
 */
public interface VaccineIntervalDao extends GenericDao<VaccineInterval, VaccineIntervalId> {
    List<VaccineInterval> findByToVaccine(Vaccine toVaccine);
    void deleteByFromVaccineId(int fromVaccineId);
}
