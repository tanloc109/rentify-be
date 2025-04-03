package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Role;
import com.vaccinex.pojo.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the UserDao interface
 */
@ApplicationScoped
public class UserDaoImpl extends AbstractDao<User, Integer> implements UserDao {

    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByIdAndDeletedIsFalse(Integer id) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.id = :id AND u.deleted = false", User.class);
        query.setParameter("id", id);
        
        List<User> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<User> findByRoleAndDeletedIsFalse(Role role) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.role = :role AND u.deleted = false", User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

    @Override
    public User getAccountByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        
        List<User> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public Optional<User> getAccountByEmailAndDeletedIsFalse(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email AND u.deleted = false", User.class);
        query.setParameter("email", email);
        
        List<User> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }
}