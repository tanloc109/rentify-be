package com.vaccinex.dao;


import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Role;
import com.vaccinex.pojo.User;

import java.util.List;
import java.util.Optional;

/**
 * User Data Access Object interface
 */
public interface UserDao extends GenericDao<User, Integer> {
    Optional<User> findByIdAndDeletedIsFalse(Integer id);
    List<User> findByRoleAndDeletedIsFalse(Role role);
    User getAccountByEmail(String email);
    Optional<User> getAccountByEmailAndDeletedIsFalse(String email);
}
