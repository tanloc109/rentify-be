package com.vaccinex.thirdparty.kafka;

import com.sba301.vaccinex.dto.internal.kafka.ScheduleMailReminderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final EmailService emailService;

    @KafkaListener(topics = "vaccine-topic", groupId = "vaccine-email-group")
    public void listen(ScheduleMailReminderDTO reminderDTO) {
        // For each message, send an email
        emailService.sendReminderEmail(reminderDTO);
    }

}
