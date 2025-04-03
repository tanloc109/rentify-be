package com.vaccinex.service;

import com.vaccinex.dao.BatchDao;
import com.vaccinex.dao.VaccineScheduleDao;
import com.vaccinex.dto.response.BatchWithRemaining;
import com.vaccinex.dto.response.VaccineInventoryAlert;
import com.vaccinex.dto.response.VaccineStockRequirement;
import com.vaccinex.pojo.Batch;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineSchedule;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import jakarta.ejb.Stateless;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
@RequiredArgsConstructor
public class VaccineInventoryNotificationServiceImpl implements VaccineInventoryNotificationService {

    private final VaccineScheduleDao vaccineScheduleRepository;
    private final BatchDao batchRepository;

    @Override
    public List<VaccineInventoryAlert> getVaccineInventoryAlerts(Integer days) {
        // Default to 14 days if not specified
        int daysToCheck = (days == null || days <= 0) ? 14 : days;

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(daysToCheck);

        // Get all scheduled appointments within the specified date range
        List<VaccineSchedule> upcomingSchedules = vaccineScheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getStatus() == VaccineScheduleStatus.PLANNED)
                .filter(schedule -> {
                    LocalDate scheduleDate = schedule.getDate().toLocalDate();
                    return !scheduleDate.isBefore(today) && !scheduleDate.isAfter(endDate);
                })
                .toList();

        // Group by vaccine and date
        Map<LocalDate, Map<Vaccine, Integer>> vaccinesByDate = new HashMap<>();

        // Initialize the map for all dates in the range
        for (int i = 0; i < daysToCheck; i++) {
            vaccinesByDate.put(today.plusDays(i), new HashMap<>());
        }

        // Count required vaccines by date
        for (VaccineSchedule schedule : upcomingSchedules) {
            LocalDate scheduleDate = schedule.getDate().toLocalDate();
            Vaccine vaccine = schedule.getVaccine();

            Map<Vaccine, Integer> vaccinesForDay = vaccinesByDate.get(scheduleDate);
            vaccinesForDay.put(vaccine, vaccinesForDay.getOrDefault(vaccine, 0) + 1);
        }

        // Get current inventory directly from batches
        Map<Vaccine, List<BatchWithRemaining>> currentInventory = new HashMap<>();

        // Get all non-expired batches with quantity > 0
        List<Batch> availableBatches = batchRepository.findAll().stream()
                .filter(batch -> batch.getQuantity() > 0)
                .filter(batch -> batch.getExpiration().isAfter(LocalDateTime.now()))
                .toList();

        for (Batch batch : availableBatches) {
            Vaccine vaccine = batch.getVaccine();

            // Create a wrapper object with the batch quantity
            BatchWithRemaining batchWithRemaining = new BatchWithRemaining(
                    batch.getId(),
                    batch.getBatchCode(),
                    batch.getImported(),
                    batch.getExpiration(),
                    vaccine,
                    batch.getQuantity()
            );

            // Add to inventory map
            if (!currentInventory.containsKey(vaccine)) {
                currentInventory.put(vaccine, new ArrayList<>());
            }

            currentInventory.get(vaccine).add(batchWithRemaining);
        }

        // Sort batches by imported date (FIFO order)
        for (List<BatchWithRemaining> batches : currentInventory.values()) {
            batches.sort(Comparator.comparing(BatchWithRemaining::getImported));
        }

        // Calculate total available quantities per vaccine
        Map<Vaccine, Integer> totalAvailable = new HashMap<>();
        for (Map.Entry<Vaccine, List<BatchWithRemaining>> entry : currentInventory.entrySet()) {
            int total = entry.getValue().stream()
                    .mapToInt(BatchWithRemaining::getQuantity)
                    .sum();
            totalAvailable.put(entry.getKey(), total);
        }

        // Generate alerts
        List<VaccineInventoryAlert> alerts = new ArrayList<>();
        Map<Vaccine, List<BatchWithRemaining>> runningInventory = new HashMap<>();

        // Deep copy of current inventory for running calculations
        for (Map.Entry<Vaccine, List<BatchWithRemaining>> entry : currentInventory.entrySet()) {
            List<BatchWithRemaining> batchesCopy = new ArrayList<>();
            for (BatchWithRemaining batch : entry.getValue()) {
                BatchWithRemaining batchCopy = new BatchWithRemaining(
                        batch.getId(),
                        batch.getBatchCode(),
                        batch.getImported(),
                        batch.getExpiration(),
                        batch.getVaccine(),
                        batch.getQuantity()
                );
                batchesCopy.add(batchCopy);
            }
            runningInventory.put(entry.getKey(), batchesCopy);
        }

        for (int i = 0; i < daysToCheck; i++) {
            LocalDate currentDate = today.plusDays(i);
            Map<Vaccine, Integer> requiredForDay = vaccinesByDate.get(currentDate);
            List<VaccineStockRequirement> requirements = new ArrayList<>();

            for (Map.Entry<Vaccine, Integer> entry : requiredForDay.entrySet()) {
                Vaccine vaccine = entry.getKey();
                int required = entry.getValue();

                // Calculate available from running inventory using FIFO
                List<BatchWithRemaining> batches = runningInventory.getOrDefault(vaccine, new ArrayList<>());
                int available = batches.stream().mapToInt(BatchWithRemaining::getQuantity).sum();
                int shortage = Math.max(0, required - available);

                // Update running inventory for next day using FIFO
                if (required > 0 && !batches.isEmpty()) {
                    int remaining = required;

                    // Remove vaccines from oldest batches first (FIFO)
                    List<BatchWithRemaining> updatedBatches = new ArrayList<>();
                    for (BatchWithRemaining batch : batches) {
                        if (remaining <= 0) {
                            // No more needed, keep batch as is
                            updatedBatches.add(batch);
                        } else if (batch.getQuantity() <= remaining) {
                            // Use entire batch
                            remaining -= batch.getQuantity();
                            // Don't add this batch as it's completely used
                        } else {
                            // Partially use batch
                            int newQuantity = batch.getQuantity() - remaining;
                            remaining = 0;
                            BatchWithRemaining updatedBatch = new BatchWithRemaining(
                                    batch.getId(),
                                    batch.getBatchCode(),
                                    batch.getImported(),
                                    batch.getExpiration(),
                                    batch.getVaccine(),
                                    newQuantity
                            );
                            updatedBatches.add(updatedBatch);
                        }
                    }

                    // Update running inventory with remaining batches
                    runningInventory.put(vaccine, updatedBatches);
                }

                if (required > 0) {
                    requirements.add(VaccineStockRequirement.builder()
                            .vaccineId(vaccine.getId())
                            .vaccineCode(vaccine.getVaccineCode())
                            .vaccineName(vaccine.getName())
                            .required(required)
                            .available(available)
                            .shortage(shortage)
                            .isShortage(shortage > 0)
                            .build());
                }
            }

            // Only add dates that have scheduled vaccines
            if (!requirements.isEmpty()) {
                alerts.add(VaccineInventoryAlert.builder()
                        .date(currentDate)
                        .daysFromNow(i)
                        .vaccineRequirements(requirements.stream()
                                .sorted(Comparator.comparing(VaccineStockRequirement::isShortage).reversed())
                                .collect(Collectors.toList()))
                        .build());
            }
        }

        return alerts;
    }
}