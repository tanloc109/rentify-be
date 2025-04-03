package com.vaccinex.service;

import com.vaccinex.dto.request.VaccineCreateRequest;
import com.vaccinex.dto.request.VaccineUpdateRequest;
import com.vaccinex.dto.response.VaccineDTO;
import com.vaccinex.dto.response.VaccineResponseDTO;
import com.vaccinex.pojo.Vaccine;

import java.util.List;

public interface VaccineService extends BaseService<Vaccine, Integer> {
    List<VaccineResponseDTO> getAllVaccines();

    List<VaccineResponseDTO> getAllVaccineActive();

    VaccineResponseDTO undeleteVaccine(Integer vaccineID);

    VaccineResponseDTO createVaccine(VaccineCreateRequest vaccineCreateRequest);

    VaccineResponseDTO updateVaccine(VaccineUpdateRequest vaccineUpdateRequest, int vaccineID);

    List<VaccineResponseDTO> searchVaccines(String name, String purpose, String price, Integer minAge, Integer maxAge);

    VaccineResponseDTO deleteVaccine(Integer vaccineID);

    Vaccine getVaccineById(Integer vaccineID);

    List<VaccineDTO> getVaccines();

    List<VaccineResponseDTO> getVaccinesV2();

    List<VaccineResponseDTO> getVaccinesActiveV2();
}