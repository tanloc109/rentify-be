package com.vaccinex.service;

import com.vaccinex.dao.UserDao;
import com.vaccinex.dto.paging.PagingResponse;
import com.vaccinex.dto.request.AccountRegisterRequest;
import com.vaccinex.dto.response.AccountDTO;
import com.vaccinex.dto.response.DoctorResponseDTO;
import com.vaccinex.dto.response.TokenResponse;
import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.mapper.AccountMapper;
import com.vaccinex.pojo.User;
import com.vaccinex.pojo.enums.EnumRoleNameType;
import com.vaccinex.pojo.enums.EnumTokenType;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Stateless
@Slf4j
public class AccountServiceImpl extends BaseServiceImpl<User, Integer> implements AccountService {

    private final UserDao userRepository;
    private final RoleService roleService;
    private final BCrypt bCryptPasswordEncoder;
    private final JWTTo jwtToken;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationManager authenticationManager;

    public AccountServiceImpl(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder,
                              JWTToken jwtToken, JWTAuthenticationFilter jwtAuthenticationFilter, AuthenticationManager authenticationManager) {
        super(userRepository);
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtToken = jwtToken;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AccountDTO registerAccount(AccountRegisterRequest accountRegisterRequest) {
        User checkExistingUser = userRepository.getAccountByEmail(accountRegisterRequest.getEmail());
        if (checkExistingUser != null) {
            throw new ElementExistException("Tài khoản đã tồn tại");
        }
        Role role = roleService.getRoleByRoleName(EnumRoleNameType.ROLE_USER);

        int calculatedAge = Period.between(accountRegisterRequest.getDob(), LocalDate.now()).getYears();

        User user = User.builder()
                .email(accountRegisterRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(accountRegisterRequest.getPassword()))
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
    public TokenResponse refreshToken(String refreshToken) {
        TokenResponse tokenResponse = TokenResponse.builder()
                .code("FAILED")
                .message("Làm mới token thất bại")
                .build();
        String email = jwtToken.getEmailFromJwt(refreshToken, EnumTokenType.REFRESH_TOKEN);
        User user = userRepository.getAccountByEmail(email);
        if (user != null) {
            if (StringUtils.hasText(refreshToken) && user.getRefreshToken().equals(refreshToken)) {
                if (jwtToken.validate(refreshToken, EnumTokenType.REFRESH_TOKEN)) {
                    CustomAccountDetail customAccountDetail = CustomAccountDetail.mapAccountToAccountDetail(user);
                    if (customAccountDetail != null) {
                        String newToken = jwtToken.generatedToken(customAccountDetail);
                        user.setAccessToken(newToken);
                        userRepository.save(user);
                        tokenResponse = TokenResponse.builder()
                                .code("Success")
                                .message("Success")
                                .userId(user.getId())
                                .token(newToken)
                                .refreshToken(refreshToken)
                                .build();
                    }
                }
            }
        }
        return tokenResponse;
    }

    @Override
    public TokenResponse login(String email, String password) {
        TokenResponse tokenResponse = TokenResponse.builder()
                .code("FAILED")
                .message("Làm mới token thất bại")
                .build();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        CustomAccountDetail accountDetail = (CustomAccountDetail) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String token = jwtToken.generatedToken(accountDetail);
        String refreshToken = jwtToken.generatedRefreshToken(accountDetail);
        User user = userRepository.getAccountByEmail(accountDetail.getEmail());
        if (user != null) {
            user.setRefreshToken(refreshToken);
            user.setAccessToken(token);
            userRepository.save(user);
            tokenResponse = TokenResponse.builder()
                    .code("Success")
                    .message("Success")
                    .userId(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .token(token)
                    .refreshToken(refreshToken)
                    .build();
        }
        return tokenResponse;
    }

    @Override
    public boolean logout(HttpServletRequest request) {
        String token = jwtAuthenticationFilter.getToken(request);
        String email = jwtToken.getEmailFromJwt(token, EnumTokenType.TOKEN);
        User user = userRepository.getAccountByEmail(email);
        if (user == null) {
            throw new ElementNotFoundException("Không tìm thấy tài khoản");
        }
        user.setAccessToken(null);
        user.setRefreshToken(null);
        User checkUser = userRepository.save(user);

        return checkUser.getAccessToken() == null;
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                () -> new EntityNotFoundException("Không tìm thấy người dùng")
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
    public PagingResponse findAll(int currentPage, int pageSize) {
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        var pageData = userRepository.findAll(pageable);

        return PagingResponse.builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent())
                .build();
    }

    @Override
    public User findById(Integer id) {
        return this.userRepository.findById(id).orElse(null);
    }

    @Override
    public User save(User entity) {
        return this.userRepository.save(entity);
    }
}
