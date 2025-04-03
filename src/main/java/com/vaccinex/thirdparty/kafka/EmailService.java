package com.vaccinex.thirdparty.kafka;

import com.vaccinex.dto.internal.ScheduleMailReminderDTO;
import jakarta.ejb.Stateless;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Stateless
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendReminderEmail(ScheduleMailReminderDTO reminderDTO) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // Build the email content using Thymeleaf template
            Context context = new Context();
            context.setVariable("reminder", reminderDTO);
            String htmlContent = templateEngine.process("reminder-email", context);

            helper.setText(htmlContent, true);
            helper.setTo(reminderDTO.getUserEmail());
            helper.setSubject("Vaccine Reminder: " + reminderDTO.getVaccineName());
            helper.setFrom("your_email@example.com");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

}
