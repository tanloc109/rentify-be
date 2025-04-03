package com.vaccinex.mapper;

import com.vaccinex.dto.request.ChildrenRequestDTO;
import com.vaccinex.dto.response.ChildrenResponseDTO;
import com.vaccinex.pojo.Child;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChildrenMapper {
    ChildrenMapper INSTANCE = Mappers.getMapper(ChildrenMapper.class);

    Child toEntity(ChildrenRequestDTO childRequestDTO);
    ChildrenResponseDTO toDTO(Child child);
    List<ChildrenResponseDTO> toDTOs(List<Child> child);
}
