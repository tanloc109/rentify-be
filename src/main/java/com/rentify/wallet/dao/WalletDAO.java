package com.rentify.wallet.dao;

import com.rentify.base.dao.BaseDAO;
import com.rentify.wallet.entity.Wallet;
import jakarta.ejb.Stateless;

@Stateless
public class WalletDAO extends BaseDAO<Wallet> {

    public WalletDAO() {
        super(Wallet.class);
    }

}
