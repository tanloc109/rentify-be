package com.vaccinex.base.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface for basic CRUD operations
 * @param <T> Entity type
 * @param <ID> ID type of the entity
 */
public interface GenericDao<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(T entity);
    void deleteById(ID id);
    boolean existsById(ID id);
    long count();
}