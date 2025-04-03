package com.vaccinex.mapper;

import com.vaccinex.dto.response.BatchResponse;
import com.vaccinex.pojo.Batch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface BatchMapper {
    BatchMapper INSTANCE = Mappers.getMapper(BatchMapper.class);

    @Mapping(source = "vaccine.id", target = "vaccineId")
    @Mapping(source = "vaccine.name", target = "vaccineName")
    @Mapping(source = "vaccine.vaccineCode", target = "vaccineCode")
    BatchResponse toBatchResponse(Batch batch);
}
