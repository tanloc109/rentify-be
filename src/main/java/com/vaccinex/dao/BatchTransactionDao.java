package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.BatchTransaction;
import com.vaccinex.pojo.composite.BatchTransactionId;

/**
 * BatchTransaction Data Access Object interface
 */
public interface BatchTransactionDao extends GenericDao<BatchTransaction, BatchTransactionId> {
    // Add any specific methods if needed
}
