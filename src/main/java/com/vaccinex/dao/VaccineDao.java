package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Vaccine;
import java.util.List;
import java.util.Optional;

/**
 * Vaccine Data Access Object interface
 */
public interface VaccineDao extends GenericDao<Vaccine, Integer> {
    List<Vaccine> findAllByDeletedFalse(int page, int size);
    long countByDeletedIsFalse();
    Vaccine findVaccineByVaccineCode(String vaccineCode);
    Vaccine findVaccineById(int id);
    Optional<Vaccine> findByIdAndDeletedIsFalse(Integer id);
    Double getMaxPrice();
    Integer getMaxAge();
    List<Vaccine> findByDeletedIsFalse();
}
