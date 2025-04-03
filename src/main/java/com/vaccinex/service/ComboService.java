package com.vaccinex.service;

import com.vaccinex.dto.paging.PagingRequest;
import com.vaccinex.dto.paging.PagingResponse;
import com.vaccinex.dto.request.VaccineComboCreateRequest;
import com.vaccinex.dto.request.VaccineComboUpdateRequest;
import com.vaccinex.dto.response.ComboResponseDTO;
import com.vaccinex.pojo.Combo;

public interface ComboService extends BaseService<Combo, Integer> {
    MappingJacksonValue getAllCombosV2(PagingRequest request);

    Combo getComboByIdV2(Integer id);

    Combo getComboById(Integer id);

    PagingResponse getAllCombos(Integer currentPage, Integer pageSize);

    PagingResponse getAllCombosActive(Integer currentPage, Integer pageSize);

    PagingResponse searchVaccineCombos(Integer currentPage, Integer pageSize, String name, String price, Integer minAge, Integer maxAge, String sortBy);

    ComboResponseDTO undeleteCombo(Integer comboID);

    ComboResponseDTO createCombo(VaccineComboCreateRequest vaccineComboCreateRequest);

    ComboResponseDTO updateCombo(VaccineComboUpdateRequest vaccineComboUpdateRequest, int vaccineID);

    ComboResponseDTO deleteCombo(Integer comboID);
}
