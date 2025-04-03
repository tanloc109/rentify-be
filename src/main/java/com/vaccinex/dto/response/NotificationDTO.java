package com.vaccinex.dto.response;

import com.vaccinex.pojo.Notification;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationDTO {
    Integer id;
    String message;
    LocalDateTime date;
    LocalDateTime scheduleDate;
    String vaccineName;
    String childName;

    public static NotificationDTO fromEntity(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .date(notification.getDate())
                .scheduleDate(notification.getSchedule().getDate())
                .childName(notification.getSchedule().getChild().getFullName())
                .vaccineName(notification.getSchedule().getVaccine().getName())
                .build();
    }
}
