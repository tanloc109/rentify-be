package com.vaccinex.service;

import com.vaccinex.dto.paging.PagingResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import java.util.List;

public abstract class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    public BaseServiceImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    @Transactional
    public PagingResponse findAll(int currentPage, int pageSize) {
        // Create Criteria Builder
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Create Count Query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        countQuery.select(cb.count(countQuery.from(entityClass)));
        long totalElements = entityManager.createQuery(countQuery).getSingleResult();

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        // Create Main Query
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        query.select(root);

        // Create TypedQuery
        TypedQuery<T> typedQuery = entityManager.createQuery(query)
                .setFirstResult((currentPage - 1) * pageSize)
                .setMaxResults(pageSize);

        // Execute query
        List<T> resultList = typedQuery.getResultList();

        return PagingResponse.builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .data(resultList)
                .build();
    }

    @Override
    public T findById(ID id) {
        return entityManager.find(entityClass, id);
    }

    @Override
    @Transactional
    public T save(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Transactional
    @Override
    public T update(T entity) {
        return entityManager.merge(entity);
    }

    @Transactional
    @Override
    public void delete(T entity) {
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }
}