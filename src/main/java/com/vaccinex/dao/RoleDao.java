package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Role;
import com.vaccinex.pojo.enums.EnumRoleNameType;
import java.util.List;
import java.util.Optional;

/**
 * Role Data Access Object interface
 */
public interface RoleDao extends GenericDao<Role, Integer> {
    Optional<Role> findByRoleNameAndDeletedIsFalse(EnumRoleNameType name);
    Optional<Role> findByIdAndDeletedIsFalse(Integer id);
    List<Role> findAllByDeletedIsFalse();
    Role getRoleByRoleName(EnumRoleNameType role);
}
