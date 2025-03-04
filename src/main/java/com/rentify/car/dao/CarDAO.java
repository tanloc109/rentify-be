package com.rentify.car.dao;

import com.rentify.base.dao.BaseDAO;
import com.rentify.car.entity.Car;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class CarDAO extends BaseDAO<Car> {

    public CarDAO() {
        super(Car.class);
    }

    /**
     * Approach 1: Using JOIN FETCH with JPQL
     * Fetches all cars with their associated entities using LEFT JOIN FETCH
     */
    public List<Car> findAllWithJoinFetch() {
        TypedQuery<Car> query = entityManager.createQuery(
                "SELECT c FROM Car c " +
                        "LEFT JOIN FETCH c.owner " +
                        "LEFT JOIN FETCH c.brand " +
                        "LEFT JOIN FETCH c.carType",
                Car.class
        );

        return query.getResultList();
    }

    /**
     * Approach 2: Using EntityGraph with JPQL
     * Fetches all cars with their associated entities using dynamically created EntityGraph
     */
    public List<Car> findAllWithEntityGraph() {
        EntityGraph<Car> entityGraph = entityManager.createEntityGraph(Car.class);
        entityGraph.addAttributeNodes("owner", "brand", "carType");

        TypedQuery<Car> query = entityManager.createQuery("SELECT c FROM Car c", Car.class);
        query.setHint("jakarta.persistence.fetchgraph", entityGraph);

        return query.getResultList();
    }

    /**
     * Approach 3: Using Named EntityGraph with NamedQuery
     * Fetches all cars using a predefined named entity graph defined in the Car entity
     * Requires @NamedEntityGraph and @NamedQuery annotations in Car class
     */
    public List<Car> findAllWithNamedEntityGraph() {
        TypedQuery<Car> query = entityManager.createNamedQuery("Car.findAll", Car.class);
        query.setHint("jakarta.persistence.fetchgraph",
                entityManager.getEntityGraph("Car.withDetails"));

        return query.getResultList();
    }

    /**
     * Bonus: Using JOIN FETCH with condition
     */
    public List<Car> findByStatusWithJoinFetch(String status) {
        TypedQuery<Car> query = entityManager.createQuery(
                "SELECT c FROM Car c " +
                        "LEFT JOIN FETCH c.owner " +
                        "LEFT JOIN FETCH c.brand " +
                        "LEFT JOIN FETCH c.carType " +
                        "WHERE c.carStatus = :status",
                Car.class
        );

        query.setParameter("status", status);
        return query.getResultList();
    }
}