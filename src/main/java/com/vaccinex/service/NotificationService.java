package com.vaccinex.service;

import com.sba301.vaccinex.dto.response.NotificationDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getNotificationsByUserId(Integer id);
}
