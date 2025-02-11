package com.rentify.transaction.service;

import com.rentify.base.exception.BadRequestException;
import com.rentify.base.exception.IdNotFoundException;
import com.rentify.transaction.dao.TransactionDAO;
import com.rentify.transaction.dto.TransactionDTO;
import com.rentify.transaction.dto.TransactionRequestDTO;
import com.rentify.transaction.entity.Transaction;
import com.rentify.transaction.entity.TransactionType;
import com.rentify.transaction.service.mapper.TransactionMapper;
import com.rentify.user.service.UserService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class TransactionService {

    @Inject
    private TransactionDAO transactionDAO;

    @Inject
    private TransactionMapper transactionMapper;

    @Inject
    private UserService userService;

    public List<TransactionDTO> findAll() {
        return transactionMapper.toDTOs(transactionDAO.findAll().stream()
                .filter(transaction -> transaction.getDeletedAt() == null)
                .collect(Collectors.toList()));
    }

    public TransactionDTO findById(Long transactionId) {
        Transaction transaction = transactionDAO.findById(transactionId).orElseThrow(() -> new IdNotFoundException("Cannot found transaction with id: " + transactionId));
        if (transaction.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Transaction with id %s is deleted before", transactionId));
        }
        return transactionMapper.toDTO(transaction);
    }

    public TransactionDTO createTransaction(TransactionRequestDTO transactionRequestDTO) {
        Transaction transaction = transactionMapper.toEntity(transactionRequestDTO);
        transactionDAO.save(transaction);
        return transactionMapper.toDTO(transaction);
    }

    public TransactionDTO updateTransaction(Long transactionId, TransactionRequestDTO transactionUpdateDTO) {
        Transaction updateTransaction = transactionDAO.findById(transactionId).orElseThrow(() -> new IdNotFoundException("Cannot found transaction with id: " + transactionId));
        if (updateTransaction.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Transaction with id %s is deleted before", transactionId));
        }
        updateTransaction.setAmount(transactionUpdateDTO.getAmount());
        updateTransaction.setType(TransactionType.valueOf(transactionUpdateDTO.getType()));
        updateTransaction.setBalance(updateTransaction.getWallet().getBalance());
        return transactionMapper.toDTO(transactionDAO.update(updateTransaction));
    }

    public void deleteTransaction(Long transactionId) {
        Transaction deleteTransaction = transactionDAO.findById(transactionId).orElseThrow(() -> new IdNotFoundException("Cannot found transaction with id: " + transactionId));
        if (deleteTransaction.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Transaction with id %s is deleted before", transactionId));
        }
        deleteTransaction.setDeletedAt(LocalDateTime.now());
        transactionDAO.update(deleteTransaction);
    }

    public Transaction createTransaction(Long userId, TransactionRequestDTO transactionRequestDTO, TransactionType transactionType) {
        Transaction transaction = transactionMapper.toEntity(transactionRequestDTO);
        transaction.setType(transactionType);
        transactionDAO.save(transaction);
        return transaction;
    }

}
