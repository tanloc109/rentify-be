package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.BatchTransaction;
import com.vaccinex.pojo.composite.BatchTransactionId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

/**
 * Implementation of the BatchTransactionDao interface
 */
@ApplicationScoped
public class BatchTransactionDaoImpl extends AbstractDao<BatchTransaction, BatchTransactionId> implements BatchTransactionDao {

    public BatchTransactionDaoImpl() {
        super(BatchTransaction.class);
    }

    @Override
    @Transactional
    public BatchTransaction save(BatchTransaction batchTransaction) {
        if (entityManager.find(BatchTransaction.class, batchTransaction.getId()) == null) {
            entityManager.persist(batchTransaction);
            return batchTransaction;
        } else {
            return entityManager.merge(batchTransaction);
        }
    }
}