package com.vaccinex.dto.response;

import com.vaccinex.pojo.VaccineSchedule;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineScheduleDTO {
    Integer id;
    LocalDateTime date;
    VaccineScheduleStatus status;
    Integer feedback;
    Integer orderNo;
    String notes;
    Integer vaccineId;
    String vaccineName;
    String vaccineCode;
    List<String> vaccineUses;
    String vaccineDescription;
    String vaccineManufacturer;
    Integer doctorId;
    String doctorName;
    Integer customerId;
    String customerName;
    Integer orderId;
    Integer comboId;
    String comboName;
    String comboDescription;
    Integer childId;
    String childName;

    public static VaccineScheduleDTO fromEntity(VaccineSchedule schedule) {
        VaccineScheduleDTO dto = VaccineScheduleDTO.builder()
                .id(schedule.getId())
                .date(schedule.getDate())
                .status(schedule.getStatus())
                .feedback(schedule.getFeedback())
                .orderNo(schedule.getOrderNo())
                .notes(schedule.getNotes())
                .build();
        if (schedule.getDoctor() != null) {
            dto.setDoctorId(schedule.getDoctor().getId());
            dto.setDoctorName(schedule.getDoctor().getFullName());
        }
        if (schedule.getCustomer() != null) {
            dto.setCustomerId(schedule.getCustomer().getId());
            dto.setCustomerName(schedule.getCustomer().getFullName());
        }
        if (schedule.getOrder() != null) {
            dto.setOrderId(schedule.getOrder().getId());
        }
        if (schedule.getVaccine() != null) {
            dto.setVaccineId(schedule.getVaccine().getId());
            dto.setVaccineName(schedule.getVaccine().getName());
            dto.setVaccineDescription(schedule.getVaccine().getDescription());
            dto.setVaccineManufacturer(schedule.getVaccine().getManufacturer());
            if (schedule.getVaccine().getUses() != null) {
                dto.setVaccineUses(schedule.getVaccine().getUses().stream().map(
                        u -> u.getName() + ": " + u.getDescription() + "\n"
                ).toList());
            }
        }
        if (schedule.getCombo() != null) {
            dto.setComboId(schedule.getCombo().getId());
            dto.setComboName(schedule.getCombo().getName());
            dto.setComboDescription(schedule.getCombo().getDescription());
        }
        if (schedule.getChild() != null) {
            dto.setChildId(schedule.getChild().getId());
            dto.setChildName(schedule.getChild().getFullName());
        }
        return dto;
    }
}
