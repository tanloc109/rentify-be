package com.vaccinex.thirdparty.kafka;

import com.vaccinex.dto.internal.ScheduleMailReminderDTO;
import jakarta.ejb.Stateless;
import lombok.RequiredArgsConstructor;

@Stateless
@RequiredArgsConstructor
public class KafkaProducerService {

//    private final KafkaTemplate<String, ScheduleMailReminderDTO> kafkaTemplate;

    public void sendReminder(ScheduleMailReminderDTO reminderDTO) {
//        kafkaTemplate.send("vaccine-topic", reminderDTO);
    }

}
