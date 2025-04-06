package com.vaccinex.service;

import com.vaccinex.dto.response.NotificationDTO;
import com.vaccinex.pojo.Notification;
import com.vaccinex.dao.NotificationDao;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Stateless
public class NotificationServiceImpl implements NotificationService {

    @Inject
    private NotificationDao notificationRepository;

    @Override
    public List<NotificationDTO> getNotificationsByUserId(Integer id) {
        List<Notification> notifications = notificationRepository.findByScheduleCustomerIdOrderByDateDesc(id);
        return notifications.stream().map(NotificationDTO::fromEntity).toList();
    }
}
