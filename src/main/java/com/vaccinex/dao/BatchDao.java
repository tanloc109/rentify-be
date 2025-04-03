package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Batch;
import com.vaccinex.pojo.Vaccine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Batch Data Access Object interface
 */
public interface BatchDao extends GenericDao<Batch, Integer> {
    List<Batch> findByDeletedIsFalse();
    Optional<Batch> findByIdAndDeletedIsFalse(Integer id);
    List<Batch> findByVaccineOrderByExpirationAsc(Vaccine vaccine);
    List<Batch> findByVaccineIdAndExpirationBeforeAndDeletedIsFalse(Integer vaccineId, LocalDateTime date);
    List<Batch> findByVaccineIdAndExpirationAfter(Integer vaccineId, LocalDateTime appointmentDate);
    List<Batch> findByVaccineIdAndExpirationBeforeAndDeletedIsFalseOrderByExpirationAsc(Integer vaccineId, LocalDateTime date);
}