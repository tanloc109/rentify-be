package com.vaccinex.service;

import com.sba301.vaccinex.dto.request.ChildrenRequestDTO;
import com.sba301.vaccinex.dto.response.ChildrenResponseDTO;
import com.sba301.vaccinex.exception.ParseEnumException;
import com.sba301.vaccinex.pojo.Child;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

public interface ChildrenService {
    Child getChildById(Integer id);

    List<ChildrenResponseDTO> findAll();

    ChildrenResponseDTO findById(Integer childId);

    ChildrenResponseDTO createChild(ChildrenRequestDTO dto, HttpServletRequest request) throws ParseEnumException;

    ChildrenResponseDTO update(Integer childId, ChildrenRequestDTO dto, HttpServletRequest request);

    void deleteById(Integer childId);

    List<ChildrenResponseDTO> findByParentId(HttpServletRequest request);

    LocalDateTime getEarliestPossibleSchedule(Integer childId);
}
