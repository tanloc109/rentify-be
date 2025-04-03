package com.vaccinex.service;

import com.sba301.vaccinex.dto.internal.PagingRequest;
import com.sba301.vaccinex.dto.internal.PagingResponse;
import com.sba301.vaccinex.dto.request.VaccineComboCreateRequest;
import com.sba301.vaccinex.dto.request.VaccineComboUpdateRequest;
import com.sba301.vaccinex.dto.response.ComboResponseDTO;
import com.sba301.vaccinex.pojo.Combo;
import org.springframework.http.converter.json.MappingJacksonValue;

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
