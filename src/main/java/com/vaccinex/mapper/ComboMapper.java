package com.vaccinex.mapper;

import com.vaccinex.dto.response.ComboResponseDTO;
import com.vaccinex.dto.response.VaccineComboResponseDTO;
import com.vaccinex.dto.response.VaccineResponseDTO;
import com.vaccinex.dto.response.VaccineTimingResponseDTO;
import com.vaccinex.pojo.Combo;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineCombo;
import com.vaccinex.pojo.VaccineTiming;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "cdi", uses = {VaccineUseMapper.class})
public interface ComboMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "deleted", target = "deleted")
    @Mapping(source = "vaccineCombos", target = "vaccines")
    @Mapping(target = "totalQuantity", expression = "java(calculateTotalQuantity(combo))")
    ComboResponseDTO comboToComboResponseDTO(Combo combo);

    default Integer calculateTotalQuantity(Combo combo) {
        return (int) combo.getVaccineCombos().stream()
                .map(VaccineCombo::getVaccine)
                .count();
    }

    @Mapping(target = "id.vaccineId", source = "id.vaccineId")
    @Mapping(target = "id.comboId", source = "id.comboId")
    @Mapping(target = "id.orderInCombo", source = "id.orderInCombo")
    @Mapping(target = "intervalDays", source = "intervalDays")
    @Mapping(target = "vaccine", source = "vaccine")
//    @Mapping(target = "combo", source = "combo")
    @Mapping(target = "combo", ignore = true)
    VaccineComboResponseDTO vaccineComboToComboVaccineResponseDTO(VaccineCombo vaccineCombo);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "vaccineCode", source = "vaccineCode")
    @Mapping(target = "manufacturer", source = "manufacturer")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "uses", source = "uses")
    @Mapping(target = "expiresInDays", source = "expiresInDays")
    @Mapping(target = "minAge", source = "minAge")
    @Mapping(target = "vaccineTimings", source = "vaccineTimings")
    @Mapping(target = "maxAge", source = "maxAge")
    @Mapping(target = "dose", source = "dose")
    @Mapping(target = "toVaccineIntervals", source = "fromVaccineIntervals")
    VaccineResponseDTO vaccineToVaccineResponseDTO(Vaccine vaccine);

    @Mapping(source = "intervalDays", target = "daysAfterPreviousDose")
    VaccineTimingResponseDTO vaccineTimingToVaccineTimingResponseDTO(VaccineTiming vaccineTiming);

    @IterableMapping(elementTargetType = VaccineComboResponseDTO.class)
    List<VaccineComboResponseDTO> vaccineComboListToVaccineComboResponseDTOList(List<VaccineCombo> vaccineCombos);

}
