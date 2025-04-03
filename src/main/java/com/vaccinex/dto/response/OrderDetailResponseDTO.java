package com.vaccinex.dto.response;

import com.vaccinex.pojo.Order;
import com.vaccinex.pojo.enums.OrderStatus;
import com.vaccinex.pojo.enums.ServiceType;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponseDTO {
    Integer orderId;
    LocalDateTime bookDate;
    ServiceType serviceType;
    OrderStatus orderStatus;
    Double totalPrice;
    Customer customer;
    Integer childId;
    String childName;
    List<VaccineScheduleOrder> vaccineSchedules;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Customer {
        Integer id;
        String firstName;
        String lastName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class VaccineScheduleOrder {
        Integer id;
        LocalDateTime date;
        VaccineScheduleStatus status;
        Integer vaccineId;
        String vaccineName;
        Integer comboId;
        String comboName;
        Integer childId;
        String childName;

    }

    public static OrderDetailResponseDTO from(Order order) {
        OrderDetailResponseDTO dto = OrderDetailResponseDTO
                .builder()
                .orderId(order.getId())
                .bookDate(order.getBookDate())
                .serviceType(order.getServiceType())
                .orderStatus(order.getStatus())
                .totalPrice((order.getTotalPrice())/20 * 100)
                .childId(order.getChild().getId())
                .childName(order.getChild().getFullName())
                .customer(Customer.builder()
                        .id(order.getCustomer().getId())
                        .firstName(order.getCustomer().getFirstName())
                        .lastName(order.getCustomer().getLastName())
                        .build())
                .vaccineSchedules(order.getSchedules().stream()
                        .filter(vaccineSchedule -> vaccineSchedule.getOrder().getId().equals(order.getId()))
                        .filter(vaccineSchedule ->
                                vaccineSchedule.getStatus() != VaccineScheduleStatus.DRAFT)
                        .map(vaccineSchedule -> {
                                    VaccineScheduleOrder vaccineScheduleOrder = VaccineScheduleOrder.builder()
                                            .id(vaccineSchedule.getId())
                                            .status(vaccineSchedule.getStatus())
                                            .date(vaccineSchedule.getDate())
                                            .vaccineId(vaccineSchedule.getVaccine().getId())
                                            .vaccineName(vaccineSchedule.getVaccine().getName())
                                            .build();
                                    if(vaccineSchedule.getCombo() != null) {
                                        vaccineScheduleOrder.setComboId(vaccineSchedule.getCombo().getId());
                                        vaccineScheduleOrder.setComboName(vaccineSchedule.getCombo().getName());
                                    }
                                    return vaccineScheduleOrder;
                                }
                        ).toList())
                .build();

        return dto;
    }

}
