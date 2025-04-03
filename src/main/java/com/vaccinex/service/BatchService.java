package com.vaccinex.service;

import com.vaccinex.dto.paging.PagingRequest;
import com.vaccinex.dto.request.BatchCreateRequest;
import com.vaccinex.dto.request.BatchUpdateRequest;
import com.vaccinex.dto.request.VaccineReturnRequest;
import com.vaccinex.dto.response.BatchQuantityDTO;
import com.vaccinex.pojo.Batch;

import java.util.List;

public interface BatchService {

    MappingJacksonValue getAllBatches(PagingRequest request);

    Batch getBatchById(Integer id);

    List<BatchQuantityDTO> getQuantityOfVaccines();

    void createBatch(BatchCreateRequest request);

    void updateBatch(Integer batchId, BatchUpdateRequest request);

    void deleteBatch(Integer batchId);

    void returnVaccine(VaccineReturnRequest request);
}
