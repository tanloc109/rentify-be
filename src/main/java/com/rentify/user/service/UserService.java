package com.rentify.user.service;

import com.rentify.user.dao.UserDAO;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

@Stateless
public class UserService {

    @Inject
    private UserDAO userDAO;



}
