package com.vaccinex.service;

import com.vaccinex.dao.BatchDao;
import com.vaccinex.dao.NotificationDao;
import com.vaccinex.dao.VaccineScheduleDao;
import com.vaccinex.dto.internal.ScheduleMailReminderDTO;
import com.vaccinex.pojo.Batch;
import com.vaccinex.pojo.Notification;
import com.vaccinex.pojo.VaccineSchedule;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import com.vaccinex.thirdparty.kafka.KafkaProducerService;

import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Singleton
@Startup
public class BatchJobService {

    private static final Logger LOGGER = Logger.getLogger(BatchJobService.class.getName());

    @Inject
    private BatchDao batchRepository;

    @Inject
    private VaccineScheduleDao vaccineScheduleRepository;

    @Inject
    private KafkaProducerService kafkaProducerService;

    @Inject
    private NotificationDao notificationRepository;

    @Schedule(hour = "2", persistent = false)
    @Transactional
    public void assignBatchToSchedules() {
        LocalDateTime now = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endDate = now.plusDays(14).toLocalDate().atTime(23, 59, 59);

        try {
            List<VaccineSchedule> vaccineSchedules = vaccineScheduleRepository
                    .findByDeletedIsFalseAndStatusAndDateIsBetweenOrderByDateAsc(
                            VaccineScheduleStatus.PLANNED, now, endDate);

            for (VaccineSchedule vaccineSchedule : vaccineSchedules) {
                List<Batch> viableBatches = batchRepository
                        .findByVaccineIdAndExpirationBeforeAndDeletedIsFalseOrderByExpirationAsc(
                                vaccineSchedule.getVaccine().getId(), vaccineSchedule.getDate());

                int selectedBatch = 0;
                while (selectedBatch < viableBatches.size() &&
                        vaccineScheduleRepository.countBatch(viableBatches.get(selectedBatch).getId()) >=
                                viableBatches.get(selectedBatch).getQuantity()) {
                    selectedBatch++;
                }

                if (selectedBatch == viableBatches.size()) {
                    LOGGER.warning("Not enough batches for schedule " + vaccineSchedule.getId());
                } else {
                    LOGGER.info("Assigned batch id " + viableBatches.get(selectedBatch).getId() +
                            " to schedule id " + vaccineSchedule.getId());
                    vaccineSchedule.setBatch(viableBatches.get(selectedBatch));
                    vaccineScheduleRepository.save(vaccineSchedule);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Error in assignBatchToSchedules: " + e.getMessage());
        }
    }

    @Schedule(hour = "7", persistent = false)
    @Transactional
    public void remindVaccineSchedules() {
        LocalDate threeDays = LocalDate.now().plusDays(3);
        LocalDate oneDay = LocalDate.now().plusDays(1);
        LocalDate today = LocalDate.now();

        try {
            List<VaccineSchedule> schedules = vaccineScheduleRepository
                    .findByDeletedIsFalseAndStatus(VaccineScheduleStatus.PLANNED)
                    .stream()
                    .filter(s ->
                            s.getDate().toLocalDate().isEqual(threeDays) ||
                                    s.getDate().toLocalDate().equals(oneDay) ||
                                    s.getDate().toLocalDate().equals(today)
                    )
                    .toList();

            for (VaccineSchedule vaccineSchedule : schedules) {
                // Send Kafka reminder
                kafkaProducerService.sendReminder(ScheduleMailReminderDTO.fromEntity(vaccineSchedule));

                // Create notification
                Notification notification = Notification.builder()
                        .date(LocalDateTime.now())
                        .schedule(vaccineSchedule)
                        .build();

                if (vaccineSchedule.getDate().toLocalDate().isEqual(threeDays)) {
                    notification.setMessage("Notification for vaccination schedule 3 days in advance");
                } else if (vaccineSchedule.getDate().toLocalDate().isEqual(today.plusDays(2))) {
                    notification.setMessage("Notification for vaccination schedule 2 days in advance");
                } else if (vaccineSchedule.getDate().toLocalDate().equals(oneDay)) {
                    notification.setMessage("Notification for vaccination schedule 1 day in advance");
                } else {
                    notification.setMessage("Notification for today's vaccination schedule");
                }

                // Save notification
                notificationRepository.save(notification);
            }
        } catch (Exception e) {
            LOGGER.severe("Error in remindVaccineSchedules: " + e.getMessage());
        }
    }
}