package com.vaccinex.mapper;

import com.vaccinex.dto.response.VaccineUseResponseDTO;
import com.vaccinex.pojo.VaccineUse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VaccineUseMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "deleted", target = "deleted")
    VaccineUseResponseDTO vaccineUseToVaccineUseResponseDTO(VaccineUse vaccineUse);

    @Mapping(source = "id", target = "id")
    VaccineUse vaccineUseResponseDTOToVaccineUse(VaccineUseResponseDTO vaccineUseResponseDTO);

}
