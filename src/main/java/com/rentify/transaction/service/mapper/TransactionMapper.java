package com.rentify.transaction.service.mapper;

import com.rentify.transaction.dto.TransactionDTO;
import com.rentify.transaction.dto.TransactionRequestDTO;
import com.rentify.transaction.entity.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface TransactionMapper {
    TransactionDTO toDTO(Transaction transaction);
    Transaction toEntity(TransactionRequestDTO transactionRequestDTO);
    List<TransactionDTO> toDTOs(List<Transaction> transactions);
}
