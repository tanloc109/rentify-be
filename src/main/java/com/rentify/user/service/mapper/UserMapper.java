package com.rentify.user.service.mapper;

import com.rentify.auth.dto.RegisterRequestDTO;
import com.rentify.user.dto.UserDTO;
import com.rentify.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserMapper {
    UserDTO toDTO(User user);
    User toUser(RegisterRequestDTO registerRequestDTO);

}
