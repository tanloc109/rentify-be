package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionDao extends GenericDao<Transaction, Integer> {
    Optional<Transaction> findByIdAndDeletedIsFalse(Integer id);

    List<Transaction> findByDoctorId(Integer doctorId);

    List<Transaction> findTransactionsByDoctorIdAndDateRange(Integer doctorId, LocalDateTime dateAfter, LocalDateTime dateBefore);

    Transaction saveTransaction(Transaction transaction);
}