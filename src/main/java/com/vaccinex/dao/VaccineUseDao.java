package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.VaccineUse;

import java.util.List;

/**
 * VaccineUse Data Access Object interface
 */
public interface VaccineUseDao extends GenericDao<VaccineUse, Integer> {
    List<VaccineUse> findAllByDeletedFalse(int page, int size);
    long countByDeletedIsFalse();
    VaccineUse findPurposeById(int id);
    VaccineUse findPurposeByName(String name);
    List<VaccineUse> findByDeletedIsFalse();
    List<VaccineUse> findAllByIdInAndDeletedFalse(List<Integer> vaccineUseIds);
    VaccineUse findByName(String name);
}
