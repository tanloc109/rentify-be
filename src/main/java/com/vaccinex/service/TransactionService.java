package com.vaccinex.service;

import com.vaccinex.dto.paging.PagingRequest;
import com.vaccinex.dto.request.TransactionCreateRequest;
import com.vaccinex.dto.request.TransactionUpdateRequest;

public interface TransactionService {
    MappingJacksonValue getAllTransactions(PagingRequest request);
    void createTransaction(TransactionCreateRequest request);
    void updateTransaction(Integer transactionId, TransactionUpdateRequest request);
    void deleteTransaction(Integer id);
}
