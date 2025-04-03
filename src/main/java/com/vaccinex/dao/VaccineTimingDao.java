package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineTiming;

import java.util.Optional;

/**
 * VaccineTiming Data Access Object interface
 */
public interface VaccineTimingDao extends GenericDao<VaccineTiming, Integer> {
    Optional<VaccineTiming> findByIdAndDeletedIsFalse(Integer id);
    Optional<VaccineTiming> findByVaccineAndDoseNoAndDeletedIsFalse(Vaccine vaccine, int doseNo);
}
