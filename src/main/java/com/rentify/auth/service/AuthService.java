package com.rentify.auth.service;

import com.rentify.auth.dto.LoginRequestDTO;
import com.rentify.auth.dto.LoginResponseDTO;
import com.rentify.auth.dto.RegisterRequestDTO;
import com.rentify.base.exception.BadRequestException;
import com.rentify.base.security.JwtGenerator;
import com.rentify.base.security.JwtPayload;
import com.rentify.user.dao.UserDAO;
import com.rentify.user.dto.UserDTO;
import com.rentify.user.entity.Role;
import com.rentify.user.entity.User;
import com.rentify.user.service.mapper.UserMapper;
import com.rentify.wallet.service.WalletService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.rentify.auth.contants.AuthExceptionMessage.*;

@Stateless
public class AuthService {

    public static final String PASSWORD_VALIDATION_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_\\-+={}\\]|:;\"'<,>.?/]).{8,}$";

    public static final String[] VALID_ROLES = {String.valueOf(Role.RENTER), String.valueOf(Role.HOST)};

    @Inject
    private UserDAO userDAO;

    @Inject
    private JwtGenerator jwtGenerator;

    @Inject
    UserMapper userMapper;

    @Inject
    WalletService walletService;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userDAO.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new BadRequestException(INVALID_EMAIL_OR_PASSWORD));

        if (!BCrypt.checkpw(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException(INVALID_EMAIL_OR_PASSWORD);
        }

        if (user.getDeletedAt() != null) {
            throw new BadRequestException(ACCOUNT_DELETED);
        }

        String accessToken = generateJWT(user);
        return new LoginResponseDTO(accessToken, userMapper.toDTO(user));
    }

    public User validateCredential(LoginRequestDTO loginRequestDTO) throws BadRequestException {
        User user = userDAO.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new BadRequestException(INVALID_EMAIL_OR_PASSWORD));

        if (!BCrypt.checkpw(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException(INVALID_EMAIL_OR_PASSWORD);
        }

        return user;
    }

    private String generateJWT(User user) {
        JwtPayload payload = new JwtPayload(user.getEmail(), user.getRole());
        return jwtGenerator.generateToken(payload.toMap());
    }

    public UserDTO register(RegisterRequestDTO registerRequestDTO) {
        validateRegisterRequest(registerRequestDTO);

        String hashedPassword = BCrypt.hashpw(registerRequestDTO.getPassword(), BCrypt.gensalt(10));

        User user = userMapper.toUser(registerRequestDTO);
        user.setPassword(hashedPassword);
        user.setRole(Role.valueOf(registerRequestDTO.getRole().toUpperCase()));

        walletService.initWallet(userDAO.save(user));

        return userMapper.toDTO(user);
    }

    private void validateRegisterRequest(RegisterRequestDTO request) {
        Map<String, String> errors = new HashMap<>();

        if (userDAO.findByEmail(request.getEmail()).isPresent()) {
            errors.put("email", EMAIL_ALREADY_USED);
        }

        if (request.getPhoneNumber().length() != 10) {
            errors.put("phoneNumber", PHONE_NUMBER_INVALID);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            errors.put("password", PASSWORDS_DO_NOT_MATCH);
        }

        if (!Pattern.compile(PASSWORD_VALIDATION_REGEX).matcher(request.getPassword()).matches()) {
            errors.put("password", INVALID_PASSWORD_FORMAT);
        }

        String role = request.getRole().toUpperCase();
        boolean isValidRole = false;
        for (String validRole : VALID_ROLES) {
            if (validRole.equalsIgnoreCase(role)) {
                isValidRole = true;
                break;
            }
        }
        if (!isValidRole) {
            errors.put("role", INVALID_ROLE);
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException(errors);
        }
    }

}
