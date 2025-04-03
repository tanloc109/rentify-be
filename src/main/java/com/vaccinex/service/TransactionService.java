package com.vaccinex.service;

import com.sba301.vaccinex.dto.internal.PagingRequest;
import com.sba301.vaccinex.dto.request.TransactionCreateRequest;
import com.sba301.vaccinex.dto.request.TransactionUpdateRequest;
import com.sba301.vaccinex.pojo.enums.Shift;
import org.springframework.http.converter.json.MappingJacksonValue;

public interface TransactionService {
    MappingJacksonValue getAllTransactions(PagingRequest request);
    void createTransaction(TransactionCreateRequest request);
    void updateTransaction(Integer transactionId, TransactionUpdateRequest request);
    void deleteTransaction(Integer id);
}
