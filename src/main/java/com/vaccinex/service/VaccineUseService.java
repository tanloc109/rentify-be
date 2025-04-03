package com.vaccinex.service;

import com.sba301.vaccinex.dto.request.VaccineUseCreateRequest;
import com.sba301.vaccinex.dto.request.VaccineUseUpdateRequest;
import com.sba301.vaccinex.dto.internal.PagingResponse;
import com.sba301.vaccinex.dto.response.VaccineResponseDTO;
import com.sba301.vaccinex.dto.response.VaccineUseResponseDTO;
import com.sba301.vaccinex.pojo.VaccineUse;

import java.util.List;

public interface VaccineUseService extends BaseService<VaccineUse, Integer> {
    PagingResponse getAllPurposes(Integer currentPage, Integer pageSize);

    PagingResponse getAllPurposesActive(Integer currentPage, Integer pageSize);

    List<VaccineUseResponseDTO> getPurposes();

    List<VaccineUseResponseDTO> getPurposesActive();

    VaccineUseResponseDTO undeletePurpose(Integer purposeID);

    VaccineUseResponseDTO createPurpose(VaccineUseCreateRequest vaccineUseCreateRequest);

    VaccineUseResponseDTO updatePurpose(VaccineUseUpdateRequest vaccineUseUpdateRequest, int purposeID);

    VaccineUseResponseDTO deletePurpose(Integer purposeID);

    PagingResponse searchVaccineUses(Integer currentPage, Integer pageSize, String name, String sortBy);

}
