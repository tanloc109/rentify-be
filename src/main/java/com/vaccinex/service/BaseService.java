package com.vaccinex.service;

import com.vaccinex.dto.paging.PagingResponse;
import jakarta.transaction.Transactional;

public interface BaseService<T, ID> {
    PagingResponse findAll(int currentPage, int pageSize);
    T findById(ID id);
    T save(T entity);

    @Transactional
    T update(T entity);

    @Transactional
    void delete(T entity);
}