package com.vaccinex.thirdparty.kafka;

import com.sba301.vaccinex.dto.internal.kafka.ScheduleMailReminderDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
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
