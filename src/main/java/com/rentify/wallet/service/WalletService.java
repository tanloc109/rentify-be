package com.rentify.wallet.service;

import com.rentify.user.entity.User;
import com.rentify.wallet.dao.WalletDAO;
import com.rentify.wallet.entity.Wallet;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class WalletService {

    @Inject
    private WalletDAO walletDAO;

    public void initWallet(User user) {
        Wallet wallet = Wallet.builder()
                .user(user)
                .build();
        walletDAO.save(wallet);
    }

}
