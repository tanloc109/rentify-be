package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Transaction Data Access Object interface
 */
public interface TransactionDao extends GenericDao<Transaction, Integer> {
    Optional<Transaction> findByIdAndDeletedIsFalse(Integer id);
    List<Transaction> findByDoctorId(Integer doctorId);
    List<Transaction> findByDoctorIdAndDateAfterAndDateBefore(Integer doctorId, LocalDateTime dateAfter, LocalDateTime dateBefore);
}
