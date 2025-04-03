package com.vaccinex.service;

import com.vaccinex.dto.request.TransactionCreateRequest;
import com.vaccinex.dto.request.TransactionUpdateRequest;
import java.util.List;
import com.vaccinex.dto.response.TransactionResponse;

public interface TransactionService {
    List<TransactionResponse> getAllTransactions();
    void createTransaction(TransactionCreateRequest request);
    void updateTransaction(Integer transactionId, TransactionUpdateRequest request);
    void deleteTransaction(Integer id);
}