package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Child;

import java.util.List;
import java.util.Optional;

/**
 * Children Data Access Object interface
 */
public interface ChildrenDao extends GenericDao<Child, Integer> {
    Optional<Child> findByIdAndDeletedIsFalse(Integer id);
    List<Child> findAllByDeletedIsFalse();
    List<Child> findAllByGuardianIdAndDeletedIsFalse(Integer guardianId);
}
