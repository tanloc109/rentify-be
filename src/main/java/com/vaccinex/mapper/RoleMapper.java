package com.vaccinex.mapper;

import com.vaccinex.dto.request.RoleRequestDTO;
import com.vaccinex.dto.response.RoleResponseDTO;
import com.vaccinex.pojo.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    Role toEntity(RoleRequestDTO roleRequestDTO);
    RoleResponseDTO toDTO(Role role);
    List<RoleResponseDTO> toDTOs(List<Role> roles);
}
