package com.rentify.wallet.service;

import com.rentify.base.exception.IdNotFoundException;
import com.rentify.transaction.dto.TransactionRequestDTO;
import com.rentify.transaction.entity.Transaction;
import com.rentify.transaction.entity.TransactionType;
import com.rentify.transaction.service.TransactionService;
import com.rentify.user.entity.User;
import com.rentify.wallet.dao.WalletDAO;
import com.rentify.wallet.entity.Wallet;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.math.BigDecimal;

@Stateless
public class WalletService {

    @Inject
    private WalletDAO walletDAO;

    @Inject
    private TransactionService transactionService;

    public void initWallet(User user) {
        Wallet wallet = Wallet.builder()
                .user(user)
                .build();
        walletDAO.save(wallet);
    }

    public BigDecimal getBalanceByUserId(Long userId) {
        Wallet wallet = walletDAO.findByUserId(userId).orElseThrow(() -> new IdNotFoundException("Cannot found wallet of user with userId: " + userId));
        return wallet != null ? wallet.getBalance() : BigDecimal.ZERO;
    }


    public Transaction depositMoney(Long userId, TransactionRequestDTO transactionRequestDTO) {
        return transactionService.createTransaction(userId, transactionRequestDTO, TransactionType.DEPOSIT);
    }

    public Transaction depositRentMoney(Long userId, TransactionRequestDTO transactionRequestDTO) {
        return transactionService.createTransaction(userId, transactionRequestDTO, TransactionType.RENT_DEPOSIT);
    }

    public Transaction makePayment(Long userId, TransactionRequestDTO transactionRequestDTO) {
        return transactionService.createTransaction(userId, transactionRequestDTO, TransactionType.RENTAL_PAYMENT);
    }

    public Transaction refundMoney(Long userId, TransactionRequestDTO transactionRequestDTO) {
        return transactionService.createTransaction(userId, transactionRequestDTO, TransactionType.REFUND);
    }

}
