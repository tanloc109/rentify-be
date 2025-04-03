package com.vaccinex.service;

import com.vaccinex.dto.request.AccountRegisterRequest;
import com.vaccinex.dto.response.AccountDTO;
import com.vaccinex.dto.response.DoctorResponseDTO;
import com.vaccinex.dto.response.TokenResponse;
import com.vaccinex.pojo.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AccountService extends BaseService<User, Integer> {

    AccountDTO registerAccount(AccountRegisterRequest accountRegisterRequest);

    TokenResponse refreshToken(String refreshToken);

    TokenResponse login(String email, String password);

    boolean logout(HttpServletRequest request);

    User getUserById(Integer id);

    List<DoctorResponseDTO> findAllDoctors();

    DoctorResponseDTO findDoctorById(Integer doctorId);
}
