package com.vaccinex.mapper;

import com.vaccinex.dto.response.*;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineInterval;
import com.vaccinex.pojo.VaccineTiming;
import com.vaccinex.pojo.VaccineUse;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface VaccineMapper {

//    @Mapping(source = "id", target = "id")
//    @Mapping(source = "deleted", target = "deleted")
//    @Mapping(source = "activated", target = "activated")
//    @Mapping(target = "uses", source = "uses")
//    @Mapping(target = "manufacturer", source = "manufacturer")
//    @Mapping(target = "expiresInDays", source = "expiresInDays")
//    @Mapping(target = "toVaccineIntervals", source = "fromVaccineIntervals")
//    @Mapping(target = "minAge", source = "minAge")
//    @Mapping(target = "maxAge", source = "maxAge")
    VaccineResponseDTO vaccineToVaccineResponseDTO(Vaccine vaccine);

//    @Mapping(source = "id", target = "id")
//    @Mapping(target = "manufacturer", source = "manufacturer")
    VaccineReportResponseDTO vaccineToVaccineReportDTO(Vaccine vaccine);

//    @Mapping(source = "id", target = "id")
//    @Mapping(target = "uses", source = "uses")
//    @Mapping(target = "manufacturer", source = "manufacturer")
//    @Mapping(target = "expiresInDays", source = "expiresInDays")
//    @Mapping(target = "minAge", source = "minAge")
//    @Mapping(target = "maxAge", source = "maxAge")
    Vaccine vaccineResponseDTOToVaccine(VaccineResponseDTO vaccineResponseDTO);

}
