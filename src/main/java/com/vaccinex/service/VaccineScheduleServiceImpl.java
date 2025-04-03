package com.vaccinex.service;

import com.vaccinex.base.config.AppConfig;
import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.base.exception.IdNotFoundException;
import com.vaccinex.dao.*;
import com.vaccinex.dto.request.VaccineDraftRequest;
import com.vaccinex.dto.response.*;
import com.vaccinex.pojo.*;
import com.vaccinex.pojo.composite.VaccineIntervalId;
import com.vaccinex.pojo.enums.OrderStatus;
import com.vaccinex.pojo.enums.ServiceType;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class VaccineScheduleServiceImpl implements VaccineScheduleService {

    @Inject
    private VaccineScheduleDao vaccineScheduleRepository;

    @Inject
    private TransactionDao transactionRepository;

    @Inject
    private VaccineTimingService vaccineTimingService;

    @Inject
    private OrderDao orderRepository;

    @Inject
    private VaccineDao vaccineRepository;

    @Inject
    private ComboDao comboRepository;

    @Inject
    private UserDao userRepository;

    @Inject
    private BatchTransactionDao batchTransactionRepository;

    @Inject
    private VaccineIntervalDao vaccineIntervalRepository;

    @Inject
    private ChildrenDao childrenRepository;

    @Inject
    private AppointmentVerificationService appointmentVerificationService;

    private int getIntervalAfterActiveVaccine() {
        return AppConfig.getBusinessIntervalAfterActiveVaccine();
    }

    private int getIntervalAfterInactiveVaccine() {
        return AppConfig.getBusinessIntervalAfterInactiveVaccine();
    }

    @Override
    @Transactional
    public void deleteDraftSchedules(Integer childId) {
        vaccineScheduleRepository.deleteByStatusAndChildId(VaccineScheduleStatus.DRAFT, childId);
    }

    @Override
    @Transactional
    public void updateSchedule(Integer vaccineScheduleId, LocalDateTime newDate) {
        VaccineSchedule firstSchedule = vaccineScheduleRepository.findById(vaccineScheduleId).orElseThrow(
                () -> new IdNotFoundException("System error: Schedule ID " + vaccineScheduleId + " not found")
        );
        if (newDate.toLocalTime().isBefore(LocalTime.of(8, 0)) || newDate.toLocalTime().isAfter(LocalTime.of(20, 0))) {
            throw new BadRequestException("Time is outside working hours");
        }
        if (firstSchedule.getStatus() != VaccineScheduleStatus.DRAFT) {
            throw new BadRequestException("Error: Schedule is no longer in draft status");
        }
        if (firstSchedule.getDate().isAfter(newDate)) {
            throw new BadRequestException("Sorry, you cannot adjust the schedule to the past, only into the future.");
        }
        if (vaccineScheduleRepository.existsByDoctorAndDate(firstSchedule.getDoctor(), newDate)) {
            throw new BadRequestException("Error: Doctor " + firstSchedule.getDoctor().getFullName() + " already has an appointment at that time, please choose a different time");
        }

        // Calculate the time difference
        Duration duration = Duration.between(firstSchedule.getDate(), newDate);

        // Update the first schedule's date
        firstSchedule.setDate(newDate);
        vaccineScheduleRepository.save(firstSchedule);

        // Retrieve all future schedules
        Child child = firstSchedule.getChild();
        List<VaccineSchedule> schedules = vaccineScheduleRepository.findByChildIdAndDateAfterOrderByDateAsc(child.getId(), firstSchedule.getDate());
        for (VaccineSchedule schedule : schedules) {
            LocalDateTime newScheduleDate = schedule.getDate().plus(duration);
            if (newScheduleDate.toLocalTime().isBefore(LocalTime.of(8, 0))) {
                // Move to the next working day at 8 AM
                LocalDateTime updatedSchedule = newScheduleDate.withHour(8).withMinute(0);
                duration = Duration.between(updatedSchedule, schedule.getDate());
                newScheduleDate = updatedSchedule;
            } else if (newScheduleDate.toLocalTime().isAfter(LocalTime.of(20, 0))) {
                // Move to the next day at 8 AM
                LocalDateTime updatedSchedule = newScheduleDate.plusDays(1).withHour(8).withMinute(0);
                duration = Duration.between(updatedSchedule, schedule.getDate());
                newScheduleDate = updatedSchedule;
            }
            schedule.setDate(newScheduleDate);
            vaccineScheduleRepository.save(schedule);
        }
    }

    @Override
    @Transactional
    public void updateExistingSchedule(Integer vaccineScheduleId, LocalDateTime newDate) {
        VaccineSchedule firstSchedule = vaccineScheduleRepository.findById(vaccineScheduleId).orElseThrow(
                () -> new IdNotFoundException("System error: Schedule ID " + vaccineScheduleId + " not found")
        );
        if (newDate.toLocalTime().isBefore(LocalTime.of(8, 0)) || newDate.toLocalTime().isAfter(LocalTime.of(20, 0))) {
            throw new BadRequestException("Time is outside working hours");
        }
        if (firstSchedule.getDate().isAfter(newDate)) {
            throw new BadRequestException("Sorry, you cannot adjust the schedule to the past, only into the future.");
        }
        if (Duration.between(firstSchedule.getDate(), newDate).toDays() > 6) {
            throw new BadRequestException("You cannot move the schedule more than 6 days.");
        }
        if (vaccineScheduleRepository.existsByDoctorAndDate(firstSchedule.getDoctor(), newDate)) {
            throw new BadRequestException("Error: Doctor " + firstSchedule.getDoctor().getFullName() + " already has an appointment at that time, please choose a different time");
        }

        Duration duration = Duration.between(firstSchedule.getDate(), newDate);

        firstSchedule.setDate(newDate);
        vaccineScheduleRepository.save(firstSchedule);

        Child child = firstSchedule.getChild();
        List<VaccineSchedule> schedules = vaccineScheduleRepository.findByChildIdAndDateAfterOrderByDateAsc(child.getId(), firstSchedule.getDate());
        for (VaccineSchedule schedule : schedules) {
            if (schedule.getStatus() == VaccineScheduleStatus.PLANNED) {
                LocalDateTime newScheduleDate = schedule.getDate().plus(duration);
                if (newScheduleDate.toLocalTime().isBefore(LocalTime.of(8, 0))) {
                    // Move to the next working day at 8 AM
                    LocalDateTime updatedSchedule = newScheduleDate.withHour(8).withMinute(0);
                    duration = Duration.between(updatedSchedule, schedule.getDate());
                    newScheduleDate = updatedSchedule;
                } else if (newScheduleDate.toLocalTime().isAfter(LocalTime.of(20, 0))) {
                    // Move to the next day at 8 AM
                    LocalDateTime updatedSchedule = newScheduleDate.plusDays(1).withHour(8).withMinute(0);
                    duration = Duration.between(updatedSchedule, schedule.getDate());
                    newScheduleDate = updatedSchedule;
                }
                schedule.setDate(newScheduleDate);
                vaccineScheduleRepository.save(schedule);
            }
        }
    }

    @Override
    public List<DoctorScheduleResponse> getDoctorSchedule(Integer doctorId, LocalDate date) {
        return vaccineScheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getDoctor().getId().equals(doctorId))
                .filter(schedule -> date == null ? schedule.getDate().toLocalDate().equals(LocalDate.now()) : schedule.getDate().toLocalDate().equals(date))
                .map(schedule -> DoctorScheduleResponse.builder()
                        .id(schedule.getId())
                        .dateTime(schedule.getDate())
                        .vaccine(schedule.getVaccine().getName())
                        .firstName(schedule.getOrder().getChild().getFirstName())
                        .lastName(schedule.getOrder().getChild().getLastName())
                        .status(schedule.getStatus())
                        .build())
                .toList();
    }

    @Override
    public List<DoctorScheduleResponse> getDoctorHistory(Integer doctorId) {
        return vaccineScheduleRepository.findByDoctorId(doctorId).stream()
                .filter(schedule -> (schedule.getStatus() == VaccineScheduleStatus.COMPLETED || schedule.getStatus() == VaccineScheduleStatus.CANCELLED))
                .map(schedule -> DoctorScheduleResponse.builder()
                        .id(schedule.getId())
                        .dateTime(schedule.getDate())
                        .vaccine(schedule.getVaccine().getName())
                        .firstName(schedule.getOrder().getChild().getFirstName())
                        .lastName(schedule.getOrder().getChild().getLastName())
                        .status(schedule.getStatus())
                        .build())
                .toList();
    }

    @Override
    public ScheduleDetail getScheduleDetails(Integer detailId) {
        VaccineSchedule schedule = vaccineScheduleRepository.findById(detailId)
                .orElseThrow(() -> new IdNotFoundException("Schedule with ID " + detailId + " not found"));

        return ScheduleDetail.builder()
                .id(schedule.getId())
                .feedback(schedule.getFeedback())
                .date(schedule.getDate())
                .doctorName(schedule.getDoctor().getFullName())
                .child(ChildDetail.builder()
                        .id(schedule.getOrder().getChild().getId())
                        .firstName(schedule.getOrder().getChild().getFirstName())
                        .lastName(schedule.getOrder().getChild().getLastName())
                        .dob(schedule.getOrder().getChild().getDob())
                        .gender(String.valueOf(schedule.getOrder().getChild().getGender()))
                        .weight(schedule.getOrder().getChild().getWeight())
                        .height(schedule.getOrder().getChild().getHeight())
                        .bloodType(schedule.getOrder().getChild().getBloodType())
                        .healthNotes(schedule.getOrder().getChild().getHealthNote())
                        .build())
                .order(OrderDetail.builder()
                        .id(schedule.getOrder().getId())
                        .bookDate(schedule.getOrder().getBookDate())
                        .serviceType(String.valueOf(schedule.getOrder().getServiceType()))
                        .build())
                .vaccine(VaccineDetail.builder()
                        .id(schedule.getVaccine().getId())
                        .name(schedule.getVaccine().getName())
                        .vaccineCode(schedule.getVaccine().getVaccineCode())
                        .description(schedule.getVaccine().getDescription())
                        .build())
                .reactions(schedule.getReactions().stream()
                        .sorted(Comparator.comparing(Reaction::getDate).reversed())
                        .map(reaction -> ReactionDetail.builder()
                                .date(reaction.getDate())
                                .reaction(reaction.getReaction())
                                .build())
                        .toList())
                .status(schedule.getStatus())
                .pastSchedules(schedule.getOrder().getChild().getSchedules().stream()
                        .filter(s -> s.getStatus() == VaccineScheduleStatus.COMPLETED)
                        .map(pastSchedule -> PastSchedule.builder()
                                .id(pastSchedule.getId())
                                .date(pastSchedule.getDate())
                                .vaccineName(pastSchedule.getVaccine().getName())
                                .doctorId(pastSchedule.getDoctor().getId().toString())
                                .feedback(pastSchedule.getFeedback())
                                .comboName(pastSchedule.getCombo() != null ? pastSchedule.getCombo().getName() : null)
                                .reactions(pastSchedule.getReactions().stream()
                                        .map(reaction -> ReactionDetail.builder()
                                                .date(reaction.getDate())
                                                .reaction(reaction.getReaction())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public Object confirmVaccination(Integer scheduleId, Integer doctorId) {
        // Find the schedule by ID
        VaccineSchedule schedule = vaccineScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IdNotFoundException("Schedule with ID " + scheduleId + " not found"));

        // Check if the schedule has already been confirmed
        if (schedule.getStatus() == VaccineScheduleStatus.COMPLETED) {
            throw new BadRequestException("Schedule has already been completed.");
        }

        // Get list of vaccine export transactions for the doctor today
        List<Transaction> transactions = transactionRepository.findByDoctorId(
                        doctorId).stream()
                .filter(transaction -> transaction.getDate().toLocalDate().equals(LocalDate.now()))
                .toList();

        if (transactions.isEmpty()) {
            throw new BadRequestException("No vaccine transactions found for this doctor today.");
        }

        BatchTransaction selectedBatchTransaction = transactions.stream()
                .flatMap(transaction -> transaction.getBatchTransactions().stream())
                .filter(bt -> bt.getBatch().getVaccine().getId().equals(schedule.getVaccine().getId()))
                .filter(bt -> bt.getRemaining() > 0)
                .min(Comparator.comparing(bt -> bt.getBatch().getExpiration()))
                .orElseThrow(() -> new BadRequestException("No suitable vaccine batch found."));

        selectedBatchTransaction.setRemaining(selectedBatchTransaction.getRemaining() - 1);
        batchTransactionRepository.save(selectedBatchTransaction);

        // Update schedule status
        schedule.setStatus(VaccineScheduleStatus.COMPLETED);
        vaccineScheduleRepository.save(schedule);
        return null;
    }

    @Override
    @Transactional
    public void handleCallback(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IdNotFoundException("Order with ID " + orderId + " not found")
        );
        List<VaccineSchedule> draftSchedules = vaccineScheduleRepository.findByStatusAndChildId(VaccineScheduleStatus.DRAFT, order.getChild().getId());
        for (VaccineSchedule schedule : draftSchedules) {
            schedule.setStatus(VaccineScheduleStatus.PLANNED);
            schedule.setOrder(order);
        }
        vaccineScheduleRepository.saveAll(draftSchedules);
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }

    @Override
    public List<VaccineScheduleDTO> getDrafts(Integer childId) {
        List<VaccineSchedule> drafts = vaccineScheduleRepository.findByStatusAndChildId(VaccineScheduleStatus.DRAFT, childId);
        return drafts.stream().map(
                VaccineScheduleDTO::fromEntity
        ).toList();
    }

    private LocalDateTime getEarliestDatePossible(Child child) {
        // Get list of schedules of child sorted by order descending (latest date comes first)
        List<VaccineSchedule> childSchedules = vaccineScheduleRepository.findByChildIdOrderByDateDesc(child.getId()).stream().filter(
                v -> v.getStatus() != VaccineScheduleStatus.CANCELLED
        ).toList();

        // If child doesn't have previous schedules, then the earliest date you can vaccinate is today
        if (childSchedules.isEmpty()) {
            System.out.println("Child has no previous vaccination history, any date is possible");
            return LocalDateTime.now();
        }

        // Get latest schedule
        VaccineSchedule latestSchedule = childSchedules.getFirst();
        System.out.println("Child's latest schedule is on " + latestSchedule.getDate());

        return latestSchedule.getDate().plusDays(
                latestSchedule.getVaccine().isActivated()
                        ? getIntervalAfterActiveVaccine()
                        : getIntervalAfterInactiveVaccine()
        );
    }

    @Override
    @Transactional
    public List<VaccineScheduleDTO> draftSchedule(VaccineDraftRequest request) {
        // Deleting previous drafts
        deleteDraftSchedules(request.childId());

        // Find child and doctor
        Child child = childrenRepository.findById(request.childId()).orElseThrow(() -> new IdNotFoundException("Child with ID " + request.childId() + " not found"));
        User doctor = userRepository.findById(request.doctorId()).orElseThrow(() -> new IdNotFoundException("Doctor with ID " + request.doctorId() + " not found"));
        User customer = userRepository.findById(request.customerId()).orElseThrow(() -> new IdNotFoundException("Customer with ID " + request.customerId() + " not found"));

        List<VaccineSchedule> schedules;

        if (request.serviceType() == ServiceType.SINGLE) {
            // Get vaccines from ids
            List<Vaccine> vaccines = request.ids().stream().map(id -> vaccineRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                    () -> new IdNotFoundException("Vaccine with ID " + id + " not found")
            )).toList();

            LocalDateTime fourteenDaysAfter = LocalDateTime.now().plusDays(14);
            if (request.desiredDate().isBefore(fourteenDaysAfter)) {
                AppointmentVerificationResponse context = appointmentVerificationService.verifyAppointmentAvailability(vaccines.getFirst().getId(), request.desiredDate());
                if (!context.isAvailable() && !context.isCanBeRescheduled()) {
                    throw new BadRequestException(context.getMessage());
                }
            }

            // Get earliest date possible vs. desired date
            LocalDateTime earliestPossibleDate = getEarliestDatePossible(child);
            LocalDateTime firstDate = request.desiredDate().isAfter(earliestPossibleDate) ? request.desiredDate() : earliestPossibleDate;

            // Draft vaccine schedules
            schedules = draftVaccineSchedules(doctor, child, customer, firstDate, vaccines);
        } else {
            // Get vaccines from combos
            List<Combo> combos = request.ids().stream().map(id -> comboRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                    () -> new IdNotFoundException("Combo with ID " + id + " not found")
            )).toList();

            LocalDateTime fourteenDaysAfter = LocalDateTime.now().plusDays(14);
            if (request.desiredDate().isBefore(fourteenDaysAfter)) {
                AppointmentVerificationResponse context = appointmentVerificationService.verifyAppointmentAvailability(combos.getFirst().getVaccineCombos().getFirst().getVaccine().getId(), request.desiredDate());
                if (!context.isAvailable() && !context.isCanBeRescheduled()) {
                    throw new BadRequestException(context.getMessage());
                }
            }

            // Get earliest date possible vs. desired date
            LocalDateTime earliestPossibleDate = getEarliestDatePossible(child);
            LocalDateTime firstDate = request.desiredDate().isAfter(earliestPossibleDate) ? request.desiredDate() : earliestPossibleDate;

            // Draft combo schedules
            schedules = draftComboSchedules(doctor, child, customer, firstDate, combos);
        }
        return schedules.stream().map(VaccineScheduleDTO::fromEntity).toList();
    }

    @Override
    @Transactional
    public List<VaccineSchedule> draftComboSchedules(User doctor, Child child, User customer, LocalDateTime firstDate, List<Combo> combos) {
        // Notes of shift schedules
        StringBuilder notesBuilder = new StringBuilder();

        // Get first date
        LocalDateTime date = firstDate;
        List<VaccineSchedule> schedules = new ArrayList<>();

        // Save last combo to find the starting point of combo
        Combo lastCombo = null;
        for (Combo combo : combos) {
            // Position of vaccine in schedule
            int orderInCombo = 1;

            // Calculate the distance between two different combos
            if (lastCombo != null && !lastCombo.getId().equals(combo.getId())) {
                // Get interval between two different combos
                date = getComboInterval(notesBuilder, date, lastCombo, combo);
            }

            // Get each vaccine in combo
            List<VaccineCombo> vaccineCombos = combo.getVaccineCombos();
            Vaccine currentVaccine = null;
            for (VaccineCombo vaccineCombo : vaccineCombos) {
                // Check if child is qualified to vaccinate
                if (currentVaccine == null || !currentVaccine.getId().equals(vaccineCombo.getVaccine().getId())) {
                    currentVaccine = vaccineCombo.getVaccine();
                    date = checkRequiredVaccines(notesBuilder, child, date, currentVaccine);
                }

                // Add interval
                date = date.plusDays(vaccineCombo.getIntervalDays());

                // Shift schedule if doctor is busy
                while (vaccineScheduleRepository.existsByDoctorAndDate(doctor, date)) {
                    notesBuilder
                            .append("Doctor ")
                            .append(doctor.getFullName())
                            .append(" already has an appointment at ")
                            .append(date)
                            .append(", moving to ");
                    date = date.plusMinutes(30);
                    if (date.toLocalTime().isAfter(LocalTime.of(20, 0))) {
                        date = date.plusDays(1).with(LocalTime.of(8, 0));
                    }
                    notesBuilder.append(date)
                            .append("\n");
                }
                VaccineSchedule vaccineSchedule = VaccineSchedule.builder()
                        .id(null)
                        .date(date)
                        .status(VaccineScheduleStatus.DRAFT)
                        .feedback(null)
                        .vaccine(vaccineCombo.getVaccine())
                        .doctor(doctor)
                        .combo(combo)
                        .child(child)
                        .customer(customer)
                        .orderNo(orderInCombo++)
                        .notes(notesBuilder.toString())
                        .build();
                schedules.add(vaccineScheduleRepository.save(vaccineSchedule));
                notesBuilder = new StringBuilder();
            }
            lastCombo = combo;
        }
        return schedules;
    }

    private LocalDateTime checkRequiredVaccines(StringBuilder notesBuilder, Child child, LocalDateTime date, Vaccine currentVaccine) {
        System.out.println("Plan: Vaccinate " + currentVaccine.getName() + " on " + date);
        List<VaccineInterval> requiredVaccines = currentVaccine.getToVaccineIntervals();
        LocalDateTime finalDate = date;

        for (VaccineInterval interval : requiredVaccines) {
            LocalDateTime requiredDate = date.minusDays(interval.getDaysBetween());
            System.out.format("%s requires %s to be vaccinated before %s: \n",
                    currentVaccine.getName(),
                    interval.getFromVaccine().getName(),
                    requiredDate);

            List<VaccineSchedule> childHistoryVaccinations = vaccineScheduleRepository
                    .findByChildIdAndVaccineIdOrderByDateAsc(child.getId(), interval.getFromVaccine().getId());

            if (childHistoryVaccinations.isEmpty()) {
                System.err.println("Child has never received " + interval.getFromVaccine().getName() + " vaccine");
                throw new BadRequestException("Child has never received " + interval.getFromVaccine().getName() + " vaccine");
            }

            // Get the required vaccination
            VaccineSchedule vaccineSchedule = childHistoryVaccinations.getLast();
            LocalDateTime validDate = vaccineSchedule.getDate().plusDays(interval.getDaysBetween());

            if (!validDate.isBefore(finalDate)) {
                notesBuilder.append(String.format("Moved %s vaccination to %s to meet requirements for %s\n",
                        currentVaccine.getName(),
                        validDate,
                        interval.getFromVaccine().getName()));
                finalDate = validDate;
            }
        }

        if (child.getAge(finalDate.toLocalDate()) >= currentVaccine.getMinAge() && child.getAge(finalDate.toLocalDate()) <= currentVaccine.getMaxAge()) {
            notesBuilder
                    .append("Child age: ")
                    .append(child.getAge(finalDate.toLocalDate()))
                    .append(" meets age requirements for vaccination (")
                    .append(currentVaccine.getMinAge())
                    .append(" - ")
                    .append(currentVaccine.getMaxAge())
                    .append(")\n");
        } else {
            throw new BadRequestException("Child age " + child.getAge(finalDate.toLocalDate()) + " does not meet age requirements for " + currentVaccine.getName() + " vaccine which requires " + currentVaccine.getMinAge() + " - " + currentVaccine.getMaxAge() + " years");
        }

        return finalDate;
    }

    private LocalDateTime getComboInterval(StringBuilder notesBuilder, LocalDateTime date, Combo lastCombo, Combo newCombo) {
        // Get last vaccine from the last combo
        Vaccine lastVaccineFromLastCombo = lastCombo.getVaccineCombos().getLast().getVaccine();

        // Get the first vaccine from the currentCombo
        Vaccine firstVaccineFromCurrentCombo = newCombo.getVaccineCombos().getFirst().getVaccine();

        // Get find if there is interval between vaccine of previous combo
        VaccineIntervalId id = VaccineIntervalId.builder()
                .fromVaccineId(lastVaccineFromLastCombo.getId())
                .toVaccineId(firstVaccineFromCurrentCombo.getId())
                .build();

        Optional<VaccineInterval> interval = vaccineIntervalRepository.findById(id);
        if (interval.isPresent()) {
            notesBuilder
                    .append("Combo ")
                    .append(lastCombo.getName())
                    .append(" --> ")
                    .append("Combo ")
                    .append(newCombo.getName())
                    .append(": Requires interval of ")
                    .append(interval.get().getDaysBetween())
                    .append(" days, moving from ")
                    .append(date);
            date = date.plusDays(interval.get().getDaysBetween());
            notesBuilder.append(" to ")
                    .append(date)
                    .append("\n");
        } else {
            if (lastVaccineFromLastCombo.isActivated()) {
                notesBuilder
                        .append("Combo ")
                        .append(lastCombo.getName())
                        .append(" --> ")
                        .append("Combo ")
                        .append(newCombo.getName())
                        .append(": Vaccine ")
                        .append(lastVaccineFromLastCombo.getName())
                        .append(" is a live vaccine, requires interval of ")
                        .append(getIntervalAfterActiveVaccine())
                        .append(" days, moving from ")
                        .append(date);
                date = date.plusDays(getIntervalAfterActiveVaccine());
                notesBuilder
                        .append(" to ")
                        .append(date)
                        .append("\n");
            } else {
                notesBuilder
                        .append("Combo ")
                        .append(lastCombo.getName())
                        .append(" --> ")
                        .append("Combo ")
                        .append(newCombo.getName())
                        .append(": Vaccine ")
                        .append(lastVaccineFromLastCombo.getName())
                        .append(" is an inactivated vaccine, requires interval of ")
                        .append(getIntervalAfterInactiveVaccine())
                        .append(" days, moving from ")
                        .append(date);
                date = date.plusDays(getIntervalAfterInactiveVaccine());
                notesBuilder
                        .append(" to ")
                        .append(date)
                        .append("\n");
            }
        }
        return date;
    }

    private LocalDateTime getVaccineInterval(StringBuilder notesBuilder, LocalDateTime date, Vaccine lastVaccine, Vaccine vaccine) {
        // Get find if there is interval between vaccine of previous combo/order
        VaccineIntervalId id = VaccineIntervalId.builder()
                .fromVaccineId(lastVaccine.getId())
                .toVaccineId(vaccine.getId())
                .build();
        Optional<VaccineInterval> interval = vaccineIntervalRepository.findById(id);
        if (interval.isPresent()) {
            notesBuilder
                    .append("Vaccine ")
                    .append(lastVaccine.getName())
                    .append(" --> ")
                    .append("Vaccine ")
                    .append(vaccine.getName())
                    .append(": Requires interval of ")
                    .append(interval.get().getDaysBetween())
                    .append(" days, moving from ")
                    .append(date);
            date = date.plusDays(interval.get().getDaysBetween());
            notesBuilder.append(" to ")
                    .append(date)
                    .append("\n");
        } else {
            if (lastVaccine.isActivated()) {
                notesBuilder
                        .append("Vaccine ")
                        .append(lastVaccine.getName())
                        .append(" --> ")
                        .append("Vaccine ")
                        .append(vaccine.getName())
                        .append(": Vaccine ")
                        .append(vaccine.getName())
                        .append(" is a live vaccine, requires interval of ")
                        .append(getIntervalAfterActiveVaccine())
                        .append(" days, moving from ")
                        .append(date);
                date = date.plusDays(getIntervalAfterActiveVaccine());
                notesBuilder.append(" to ")
                        .append(date)
                        .append("\n");
            } else {
                notesBuilder
                        .append("Vaccine ")
                        .append(lastVaccine.getName())
                        .append(" --> ")
                        .append("Vaccine ")
                        .append(vaccine.getName())
                        .append(": Vaccine ")
                        .append(vaccine.getName())
                        .append(" is an inactivated vaccine, requires interval of ")
                        .append(getIntervalAfterInactiveVaccine())
                        .append(" days, moving from ")
                        .append(date);
                date = date.plusDays(getIntervalAfterInactiveVaccine());
                notesBuilder.append(" to ")
                        .append(date)
                        .append("\n");
            }
        }
        return date;
    }

    @Override
    @Transactional
    public List<VaccineSchedule> draftVaccineSchedules(User doctor, Child child, User customer, LocalDateTime firstDate, List<Vaccine> vaccines) {
        // Note out the required vaccines
        StringBuilder notesBuilder = new StringBuilder();

        // Get first date
        LocalDateTime date = firstDate;
        List<VaccineSchedule> schedules = new ArrayList<>();

        // Note last vaccine to find interval between vaccines
        Vaccine lastVaccine = null;
        for (Vaccine vaccine : vaccines) {
            // Calculate the distance between two different vaccines
            if (lastVaccine != null && !vaccine.getId().equals(lastVaccine.getId())) {
                date = getVaccineInterval(notesBuilder, date, lastVaccine, vaccine);
            }

            // For each vaccine type, check if it meets vaccination requirements
            date = checkRequiredVaccines(notesBuilder, child, date, vaccine);

            int dose = vaccine.getDose();
            for (int i = 0; i < dose; i++) {
                System.err.println(i);
                VaccineTiming vaccineTiming = vaccineTimingService.getVaccineTimingByVaccine(vaccine, i + 1);
                date = date.plusDays(vaccineTiming.getIntervalDays());
                if (vaccineScheduleRepository.existsByDoctorAndDate(doctor, date)) {
                    notesBuilder
                            .append("Doctor ").append(doctor.getFullName()).append(" already has an appointment at ")
                            .append(date)
                            .append(", moving to ");
                    date = date.plusMinutes(30);
                    if (date.toLocalTime().isAfter(LocalTime.of(20, 0))) {
                        date = date.plusDays(1).with(LocalTime.of(8, 0));
                    }
                    notesBuilder.append(date)
                            .append("\n");
                }
                VaccineSchedule vaccineSchedule = VaccineSchedule.builder()
                        .id(null)
                        .date(date)
                        .status(VaccineScheduleStatus.DRAFT)
                        .feedback(null)
                        .vaccine(vaccine)
                        .orderNo(i + 1)
                        .doctor(doctor)
                        .child(child)
                        .notes(notesBuilder.toString())
                        .customer(customer)
                        .build();
                schedules.add(vaccineSchedule);
                notesBuilder = new StringBuilder();
            }
            lastVaccine = vaccine;
        }
        schedules = vaccineScheduleRepository.saveAll(schedules);
        return schedules;
    }

    @Override
    public List<CustomerScheduleResponse> getVaccinesByCustomer(Integer customerId) {
        User customer = userRepository.findByIdAndDeletedIsFalse(customerId).orElseThrow(
                () -> new BadRequestException("Customer with ID " + customerId + " not found")
        );

        List<VaccineSchedule> vaccineSchedules = vaccineScheduleRepository.findByCustomer(customer);

        return vaccineSchedules.stream()
                .map(CustomerScheduleResponse::fromEntity)
                .collect(Collectors.toList());
    }
}