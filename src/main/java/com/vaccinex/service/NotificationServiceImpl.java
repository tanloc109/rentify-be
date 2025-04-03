package com.vaccinex.service;

import com.sba301.vaccinex.dto.response.NotificationDTO;
import com.sba301.vaccinex.pojo.Notification;
import com.sba301.vaccinex.repository.NotificationRepository;
import com.sba301.vaccinex.service.spec.NotificationService;
import jakarta.ejb.Stateless;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Stateless
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationDTO> getNotificationsByUserId(Integer id) {
        List<Notification> notifications = notificationRepository.findByScheduleCustomerIdOrderByDateDesc(id);
        return notifications.stream().map(NotificationDTO::fromEntity).toList();
    }
}
