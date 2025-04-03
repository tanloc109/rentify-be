package com.vaccinex.service;

import com.vaccinex.base.filter.AuthenticationFilter;
import com.vaccinex.base.security.JwtGenerator;
import com.vaccinex.dao.UserDao;
import com.vaccinex.dto.request.AccountRegisterRequest;
import com.vaccinex.dto.response.AccountDTO;
import com.vaccinex.dto.response.DoctorResponseDTO;
import com.vaccinex.dto.response.TokenResponse;
import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.mapper.AccountMapper;
import com.vaccinex.pojo.Role;
import com.vaccinex.pojo.User;
import com.vaccinex.pojo.enums.EnumRoleNameType;
import com.vaccinex.pojo.enums.EnumTokenType;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Stateless
public class AccountServiceImpl extends BaseServiceImpl<User, Integer> implements AccountService {

    @Inject
    private UserDao userRepository;

    @Inject
    private RoleService roleService;

    @Inject
    private JwtGenerator jwtToken;

    @Inject
    private AuthenticationFilter jwtAuthenticationFilter;

    @Inject
    private SecurityContext securityContext;

    public AccountServiceImpl() {
        super(User.class);
    }

    @Override
    @Transactional
    public AccountDTO registerAccount(AccountRegisterRequest accountRegisterRequest) {
        User checkExistingUser = userRepository.getAccountByEmail(accountRegisterRequest.getEmail());
        if (checkExistingUser != null) {
            throw new ElementExistException("Account already exists");
        }
        Role role = roleService.getRoleByRoleName(EnumRoleNameType.ROLE_USER);

        int calculatedAge = Period.between(accountRegisterRequest.getDob(), LocalDate.now()).getYears();

        User user = User.builder()
                .email(accountRegisterRequest.getEmail())
                .password(BCrypt.hashpw(accountRegisterRequest.getPassword(), BCrypt.gensalt()))
                .dob(accountRegisterRequest.getDob())
                .firstName(accountRegisterRequest.getFirstName())
                .lastName(accountRegisterRequest.getLastName())
                .age(calculatedAge)
                .address(accountRegisterRequest.getAddress())
                .phone(accountRegisterRequest.getPhone())
                .accessToken(null)
                .refreshToken(null)
                .enabled(true)
                .nonLocked(true)
                .role(role)
                .build();

        return AccountMapper.INSTANCE.accountToAccountDTO(userRepository.save(user));
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        TokenResponse tokenResponse = TokenResponse.builder()
                .code("FAILED")
                .message("Token refresh failed")
                .build();

        String email = jwtToken.getEmailFromJwt(refreshToken, EnumTokenType.REFRESH_TOKEN);
        User user = userRepository.getAccountByEmail(email);

//        if (user != null) {
//            if (refreshToken != null && !refreshToken.trim().isEmpty() && refreshToken.equals(user.getRefreshToken())) {
//                if (jwtToken.validate(refreshToken, EnumTokenType.REFRESH_TOKEN)) {
//                    CustomAccountDetail customAccountDetail = CustomAccountDetail.mapAccountToAccountDetail(user);
//                    if (customAccountDetail != null) {
//                        String newToken = jwtToken.generatedToken(customAccountDetail);
//                        user.setAccessToken(newToken);
//                        userRepository.save(user);
//                        tokenResponse = TokenResponse.builder()
//                                .code("Success")
//                                .message("Success")
//                                .userId(user.getId())
//                                .token(newToken)
//                                .refreshToken(refreshToken)
//                                .build();
//                    }
//                }
//            }
//        }
        return tokenResponse;
    }

    @Override
    @Transactional
    public TokenResponse login(String email, String password) {
        TokenResponse tokenResponse = TokenResponse.builder()
                .code("FAILED")
                .message("Login failed")
                .build();

        // Using Jakarta Security for authentication
        AuthenticationStatus status = securityContext.authenticate(
                null,
                null,
                AuthenticationParameters.withParams()
                        .credential(new UsernamePasswordCredential(email, password))
        );

//        if (status == AuthenticationStatus.SUCCESS) {
//            CustomAccountDetail accountDetail = (CustomAccountDetail) securityContext.getCallerPrincipal();
//
//            String token = jwtToken.generatedToken(accountDetail);
//            String refreshToken = jwtToken.generatedRefreshToken(accountDetail);
//            User user = userRepository.getAccountByEmail(accountDetail.getEmail());
//
//            if (user != null) {
//                user.setRefreshToken(refreshToken);
//                user.setAccessToken(token);
//                userRepository.save(user);
//                tokenResponse = TokenResponse.builder()
//                        .code("Success")
//                        .message("Success")
//                        .userId(user.getId())
//                        .firstName(user.getFirstName())
//                        .lastName(user.getLastName())
//                        .token(token)
//                        .refreshToken(refreshToken)
//                        .build();
//            }
//        }

        return tokenResponse;
    }

    @Override
    @Transactional
    public boolean logout(HttpServletRequest request) {
//        String token = jwtAuthenticationFilter.getToken(request);
//        String email = jwtToken.getEmailFromJwt(token, EnumTokenType.TOKEN);
//        User user = userRepository.getAccountByEmail(email);
//
//        if (user == null) {
//            throw new ElementNotFoundException("Account not found");
//        }
//
//        user.setAccessToken(null);
//        user.setRefreshToken(null);
//        User checkUser = userRepository.save(user);
//
//        return checkUser.getAccessToken() == null;
        return true;
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                () -> new ElementNotFoundException("User not found")
        );
    }

    @Override
    public List<DoctorResponseDTO> findAllDoctors() {
        Role role = roleService.getRoleByRoleName(EnumRoleNameType.ROLE_DOCTOR);
        return AccountMapper.INSTANCE.toDoctorDTOs(userRepository.findByRoleAndDeletedIsFalse(role));
    }

    @Override
    public DoctorResponseDTO findDoctorById(Integer doctorId) {
        User doctor = getUserById(doctorId);
        return AccountMapper.INSTANCE.toDoctorDTO(doctor);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User entity) {
        return this.userRepository.save(entity);
    }
}