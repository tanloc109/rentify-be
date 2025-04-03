package com.vaccinex.service;

import com.vaccinex.dto.request.VaccineUseCreateRequest;
import com.vaccinex.dto.request.VaccineUseUpdateRequest;
import com.vaccinex.dto.response.VaccineUseResponseDTO;
import com.vaccinex.pojo.VaccineUse;

import java.util.List;

public interface VaccineUseService extends BaseService<VaccineUse, Integer> {
    List<VaccineUseResponseDTO> getAllPurposes();

    List<VaccineUseResponseDTO> getAllPurposesActive();

    List<VaccineUseResponseDTO> getPurposes();

    List<VaccineUseResponseDTO> getPurposesActive();

    VaccineUseResponseDTO undeletePurpose(Integer purposeID);

    VaccineUseResponseDTO createPurpose(VaccineUseCreateRequest vaccineUseCreateRequest);

    VaccineUseResponseDTO updatePurpose(VaccineUseUpdateRequest vaccineUseUpdateRequest, int purposeID);

    VaccineUseResponseDTO deletePurpose(Integer purposeID);

    List<VaccineUseResponseDTO> searchVaccineUses(String name);

}