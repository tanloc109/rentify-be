package com.vaccinex.service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

public interface BaseService<T, ID> {
    List<T> findAll();
    Optional<T> findById(ID id);
    T save(T entity);
    @Transactional
    T update(T entity);
    @Transactional
    void delete(T entity);
    List<T> saveAll(List<T> entities);
}