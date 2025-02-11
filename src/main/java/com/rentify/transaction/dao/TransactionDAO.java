package com.rentify.transaction.dao;

import com.rentify.base.dao.BaseDAO;
import com.rentify.transaction.entity.Transaction;
import jakarta.ejb.Stateless;

@Stateless
public class TransactionDAO extends BaseDAO<Transaction> {

    public TransactionDAO() {
        super(Transaction.class);
    }

}
