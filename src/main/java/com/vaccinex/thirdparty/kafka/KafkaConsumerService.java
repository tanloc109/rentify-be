package com.vaccinex.thirdparty.kafka;

import com.vaccinex.dto.internal.ScheduleMailReminderDTO;
import jakarta.ejb.Stateless;
import lombok.RequiredArgsConstructor;

@Stateless
@RequiredArgsConstructor
public class KafkaConsumerService {
//
//    private final EmailService emailService;
//
//    @KafkaListener(topics = "vaccine-topic", groupId = "vaccine-email-group")
    public void listen(ScheduleMailReminderDTO reminderDTO) {
//        // For each message, send an email
//        emailService.sendReminderEmail(reminderDTO);
    }

}
