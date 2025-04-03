package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Role;
import com.vaccinex.pojo.enums.EnumRoleNameType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the RoleDao interface
 */
@ApplicationScoped
public class RoleDaoImpl extends AbstractDao<Role, Integer> implements RoleDao {

    public RoleDaoImpl() {
        super(Role.class);
    }

    @Override
    public Optional<Role> findByRoleNameAndDeletedIsFalse(EnumRoleNameType name) {
        TypedQuery<Role> query = entityManager.createQuery(
                "SELECT r FROM Role r WHERE r.roleName = :name AND r.deleted = false", Role.class);
        query.setParameter("name", name);
        
        List<Role> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Role> findByIdAndDeletedIsFalse(Integer id) {
        TypedQuery<Role> query = entityManager.createQuery(
                "SELECT r FROM Role r WHERE r.id = :id AND r.deleted = false", Role.class);
        query.setParameter("id", id);
        
        List<Role> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Role> findAllByDeletedIsFalse() {
        return entityManager.createQuery(
                "SELECT r FROM Role r WHERE r.deleted = false", Role.class)
                .getResultList();
    }

    @Override
    public Role getRoleByRoleName(EnumRoleNameType role) {
        TypedQuery<Role> query = entityManager.createQuery(
                "SELECT r FROM Role r WHERE r.roleName = :role", Role.class);
        query.setParameter("role", role);
        
        List<Role> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @Transactional
    public Role save(Role role) {
        if (role.getId() == null) {
            entityManager.persist(role);
            return role;
        } else {
            return entityManager.merge(role);
        }
    }
}