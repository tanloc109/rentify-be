package com.rentify.user.dao;

import com.rentify.base.dao.BaseDAO;
import com.rentify.user.entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

@Stateless
public class UserDAO extends BaseDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByEmail(@NotNull @NotBlank String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(entityClass);
        Root<User> root = cq.from(entityClass);
        cq.select(root).where(cb.equal(root.get("email"), email.trim()));
        TypedQuery<User> query = entityManager.createQuery(cq);
        List<User> result = query.getResultList();
        return result.stream().findFirst();
    }

}
