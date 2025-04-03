package com.vaccinex.service;

import com.sba301.vaccinex.dto.response.VaccineDTO;
import com.sba301.vaccinex.dto.request.VaccineCreateRequest;
import com.sba301.vaccinex.dto.request.VaccineUpdateRequest;
import com.sba301.vaccinex.dto.internal.PagingResponse;
import com.sba301.vaccinex.dto.response.VaccineResponseDTO;
import com.sba301.vaccinex.pojo.Vaccine;

import java.util.List;

public interface VaccineService extends BaseService<Vaccine, Integer> {
    PagingResponse getAllVaccines(Integer currentPage, Integer pageSize);

    PagingResponse getAllVaccineActive(Integer currentPage, Integer pageSize);

    VaccineResponseDTO undeleteVaccine(Integer vaccineID);

    VaccineResponseDTO createVaccine(VaccineCreateRequest vaccineCreateRequest);

    VaccineResponseDTO updateVaccine(VaccineUpdateRequest vaccineUpdateRequest, int vaccineID);

    PagingResponse searchVaccines(Integer currentPage, Integer pageSize, String name, String purpose, String price, Integer minAge, Integer maxAge, String sortBy);

    VaccineResponseDTO deleteVaccine(Integer vaccineID);

    Vaccine getVaccineById(Integer vaccineID);

    List<VaccineDTO> getVaccines();

    List<VaccineResponseDTO> getVaccinesV2();

    List<VaccineResponseDTO> getVaccinesActiveV2();

}
