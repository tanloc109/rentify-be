package com.vaccinex.mapper;

import com.vaccinex.dto.response.VaccineTimingResponseDTO;
import com.vaccinex.pojo.VaccineTiming;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VaccineTimingMapper {

    @Mapping(source = "intervalDays", target = "daysAfterPreviousDose")
    VaccineTimingResponseDTO vaccineTimingToVaccineTimingResponseDTO(VaccineTiming vaccineTiming);

}
