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

@Mapper(componentModel = "spring")
public interface VaccineMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "deleted", target = "deleted")
    @Mapping(source = "activated", target = "activated")
    @Mapping(target = "uses", source = "uses")
    @Mapping(target = "manufacturer", source = "manufacturer")
    @Mapping(target = "expiresInDays", source = "expiresInDays")
    @Mapping(target = "toVaccineIntervals", source = "fromVaccineIntervals")
    @Mapping(target = "minAge", source = "minAge")
    @Mapping(target = "maxAge", source = "maxAge")
    VaccineResponseDTO vaccineToVaccineResponseDTO(Vaccine vaccine);

    @Mapping(source = "id", target = "id")
    @Mapping(target = "manufacturer", source = "manufacturer")
    VaccineReportResponseDTO vaccineToVaccineReportDTO(Vaccine vaccine);

    @Mapping(source = "id", target = "id")
    @Mapping(target = "uses", source = "uses")
    @Mapping(target = "manufacturer", source = "manufacturer")
    @Mapping(target = "expiresInDays", source = "expiresInDays")
    @Mapping(target = "minAge", source = "minAge")
    @Mapping(target = "maxAge", source = "maxAge")
    Vaccine vaccineResponseDTOToVaccine(VaccineResponseDTO vaccineResponseDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "deleted", source = "deleted")
    VaccineUseResponseDTO vaccineUseToVaccineUseResponseDTO(VaccineUse vaccineUse);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "doseNo", target = "doseNo")
    @Mapping(source = "intervalDays", target = "daysAfterPreviousDose")
    VaccineTimingResponseDTO vaccineTimingToVaccineTimingResponseDTO(VaccineTiming vaccineTiming);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "doseNo", target = "doseNo")
    @Mapping(target = "intervalDays", source = "daysAfterPreviousDose")
    VaccineTiming VaccineTimingResponseDTOToVaccineTiming(VaccineTimingResponseDTO vaccineTiming);

    @Mapping(target = "id.fromVaccineId", source = "id.fromVaccineId")
    @Mapping(target = "id.toVaccineId", source = "id.toVaccineId")
    @Mapping(target = "toVaccine", source = "toVaccine", qualifiedByName = "mapVaccine")
    @Mapping(target = "daysBetween", source = "daysBetween")
    VaccineIntervalResponseDTO vaccineIntervalToVaccineIntervalResponseDTO(VaccineInterval vaccineInterval);

    @IterableMapping(elementTargetType = VaccineIntervalResponseDTO.class)
    List<VaccineIntervalResponseDTO> vaccineIntervalListToVaccineIntervalResponseDTOList(List<VaccineInterval> vaccineInterval);

    @Named("mapVaccine")
    default VaccineResponseIntervalCustomDTO mapVaccine(Vaccine vaccine) {
        if (vaccine == null) return null;
        return new VaccineResponseIntervalCustomDTO(vaccine.getId(), vaccine.isDeleted(), vaccine.getName(), vaccine.getVaccineCode(), vaccine.isActivated());
    }

}
