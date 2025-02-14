package com.rentify.user.service;

import com.rentify.base.exception.BadRequestException;
import com.rentify.base.exception.IdNotFoundException;
import com.rentify.user.dao.UserDAO;
import com.rentify.user.dto.UserDTO;
import com.rentify.user.dto.UserUpdateRequest;
import com.rentify.user.entity.User;
import com.rentify.user.service.mapper.UserMapper;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class UserService {

    @Inject
    private UserDAO userDAO;

    @Inject
    private UserMapper userMapper;

    public List<UserDTO> findAll() {
        return userMapper.toDTOs(userDAO.findAll().stream()
                .filter(user -> user.getDeletedAt() == null)
                .collect(Collectors.toList()));
    }

    public UserDTO findById(Long userId) {
        User user = userDAO.findById(userId).orElseThrow(() -> new IdNotFoundException("Cannot found user with id: " + userId));
        if (user.getDeletedAt() != null) {
            throw new BadRequestException(String.format("User with id %s is deleted before", userId));
        }
        return userMapper.toDTO(user);
    }

    public UserDTO findByEmail(String email) {
        User user = userDAO.getByEmail(email).orElseThrow(() -> new IdNotFoundException("Cannot found user with email: " + email));
        if (user.getDeletedAt() != null) {
            throw new BadRequestException(String.format("User with email %s is deleted before", email));
        }
        return userMapper.toDTO(user);
    }

    public UserDTO updateUser(Long userId, UserUpdateRequest userDTO) {
        User updateUser = userDAO.findById(userId).orElseThrow(() -> new IdNotFoundException("Cannot found user with id: " + userId));
        if (updateUser.getDeletedAt() != null) {
            throw new BadRequestException(String.format("User with id %s is deleted before", userId));
        }
        updateUser.setFullName(userDTO.getFullName());
        return userMapper.toDTO(userDAO.update(updateUser));
    }

    public void deleteUser(Long userId) {
        User deleteUser = userDAO.findById(userId).orElseThrow(() -> new IdNotFoundException("Cannot found user with id: " + userId));
        if (deleteUser.getDeletedAt() != null) {
            throw new BadRequestException(String.format("User with id %s is deleted before", userId));
        }
        deleteUser.setDeletedAt(LocalDateTime.now());
        userDAO.update(deleteUser);
    }

}
