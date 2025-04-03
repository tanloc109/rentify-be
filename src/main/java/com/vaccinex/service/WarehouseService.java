package com.vaccinex.service;

import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.dto.request.ExportVaccineRequest;
import com.vaccinex.dto.response.VaccineReportResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface WarehouseService {
    List<VaccineReportResponseDTO> getVaccineReports(Integer doctorId, String shift, LocalDate date);

    Object requestVaccineExport(ExportVaccineRequest request) throws BadRequestException;
}
