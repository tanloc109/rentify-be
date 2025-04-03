package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Combo;

import java.util.List;
import java.util.Optional;

/**
 * Combo Data Access Object interface
 */
public interface ComboDao extends GenericDao<Combo, Integer> {
    List<Combo> findAllByDeletedFalse(int page, int size);
    List<Combo> findByDeletedIsFalse(int page, int size);
    Optional<Combo> findByIdAndDeletedIsFalse(Integer id);
    Combo findComboByName(String name);
    Combo findComboById(int id);
    Double getMaxPrice();
    Integer getMaxAge();
    long countByDeletedIsFalse();
}
