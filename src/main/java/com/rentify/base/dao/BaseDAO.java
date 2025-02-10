package com.rentify.base.dao;

import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public abstract class BaseDAO<T> {

    @PersistenceContext
    protected EntityManager entityManager;
    protected final Class<T> entityClass;
    private static final Integer BATCH_SIZE = 50;

    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<T> q = cb.createQuery(entityClass);
        q.from(entityClass);

        return entityManager.createQuery(q).getResultList();
    }

    public Optional<T> findById(long id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    public T update(T entity) {
        entityManager.merge(entity);
        return entity;
    }

    public void delete(T entity) {
        entityManager.remove(entity);
    }

    public Optional<T> findById(Long id, LockModeType lockModeType) {
        return Optional.ofNullable(entityManager.find(entityClass, id, lockModeType));
    }

    public void flush() {
        entityManager.flush();
    }

    public List<T> addAll(List<T> entities) {
        IntStream.range(0, entities.size()).forEach(index -> {
            entityManager.persist(entities.get(index));
            if (index % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        });
        entityManager.flush();
        entityManager.clear();
        return entities;
    }
}
