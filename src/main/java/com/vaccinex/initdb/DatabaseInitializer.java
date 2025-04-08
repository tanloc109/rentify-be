package com.vaccinex.initdb;

import com.vaccinex.pojo.*;
import com.vaccinex.pojo.composite.VaccineComboId;
import com.vaccinex.pojo.composite.VaccineIntervalId;
import com.vaccinex.pojo.enums.*;
import com.vaccinex.dao.*;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Singleton
@Startup
public class DatabaseInitializer {
    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class.getName());

    @Inject
    private VaccineDao vaccineRepository;

    @Inject
    private VaccineComboDao vaccineComboRepository;

    @Inject
    private ComboDao comboRepository;

    @Inject
    private VaccineUseDao vaccineUseRepository;

    @Inject
    private VaccineTimingDao vaccineTimingRepository;

    @Inject
    private RoleDao roleRepository;

    @Inject
    private UserDao userRepository;

    @Inject
    private ChildrenDao childrenRepository;

    @Inject
    private BatchDao batchRepository;

    @Inject
    private VaccineIntervalDao vaccineIntervalRepository;

    @Inject
    private VaccineScheduleDao vaccineScheduleRepository;

    @Inject
    private OrderDao orderRepository;

    @PostConstruct
    @Transactional
    public void initDatabase() {
        try {
            if (roleRepository.count() > 0) {
                logger.info("Database already initialized, skipping initialization");
                return;
            }

            // Initialize standard roles
            Role roleAdmin = new Role(null, EnumRoleNameType.ROLE_ADMIN, "ADMIN", null);
            Role roleDoctor = new Role(null, EnumRoleNameType.ROLE_DOCTOR, "DOCTOR", null);
            Role roleUser = new Role(null, EnumRoleNameType.ROLE_USER, "USER", null);
            Role roleStaff = new Role(null, EnumRoleNameType.ROLE_STAFF, "STAFF", null);

            roleRepository.save(roleAdmin);
            roleRepository.save(roleDoctor);
            roleRepository.save(roleUser);
            roleRepository.save(roleStaff);

            // Initialize standard users
            User user = User.builder()
                    .firstName("John")
                    .lastName("Smith")
                    .email("user@gmail.com")
                    .phone("+15551234567")
                    .password(BCrypt.hashpw("123456", BCrypt.gensalt()))
                    .enabled(true)
                    .address("123 Main Street")
                    .accessToken(null)
                    .refreshToken(null)
                    .nonLocked(true)
                    .role(roleUser)
                    .build();
            userRepository.save(user);

            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .email("admin@gmail.com")
                    .phone("+15551234568")  // Added phone number
                    .password(BCrypt.hashpw("123456", BCrypt.gensalt()))
                    .enabled(true)
                    .accessToken(null)
                    .refreshToken(null)
                    .nonLocked(true)
                    .role(roleAdmin)
                    .build();
            userRepository.save(admin);

            User doctor = User.builder()
                    .firstName("David")
                    .lastName("Jones")
                    .email("doctor@gmail.com")
                    .phone("+15551234569")  // Added phone number
                    .password(BCrypt.hashpw("123456", BCrypt.gensalt()))
                    .enabled(true)
                    .accessToken(null)
                    .refreshToken(null)
                    .nonLocked(true)
                    .role(roleDoctor)
                    .build();
            userRepository.save(doctor);

            User doctor2 = User.builder()
                    .firstName("Sarah")
                    .lastName("Williams")
                    .email("doctor2@gmail.com")
                    .phone("+15551234570")  // Added phone number
                    .password(BCrypt.hashpw("123456", BCrypt.gensalt()))
                    .enabled(true)
                    .accessToken(null)
                    .refreshToken(null)
                    .nonLocked(true)
                    .role(roleDoctor)
                    .build();
            userRepository.save(doctor2);

            User staff = User.builder()
                    .firstName("Michael")
                    .lastName("Taylor")
                    .email("staff@gmail.com")
                    .phone("+15551234571")  // Added phone number
                    .password(BCrypt.hashpw("123456", BCrypt.gensalt()))
                    .enabled(true)
                    .accessToken(null)
                    .refreshToken(null)
                    .nonLocked(true)
                    .role(roleStaff)
                    .build();
            userRepository.save(staff);

            // Create test data for children with appropriate ages for vaccination
            Child john = Child.builder()
                    .firstName("William")
                    .lastName("Smith")
                    .dob(LocalDate.now().minusYears(2).minusMonths(1))
                    .gender(Gender.MALE)
                    .weight(12.5)
                    .height(86.0)
                    .bloodType("A+")
                    .healthNote("Healthy child, no underlying conditions")
                    .guardian(user)
                    .build();

            Child emma = Child.builder()
                    .firstName("Emma")
                    .lastName("Smith")
                    .dob(LocalDate.now().minusYears(2))
                    .gender(Gender.FEMALE)
                    .weight(10.2)
                    .height(76.5)
                    .bloodType("O+")
                    .healthNote("Mild eczema history")
                    .guardian(user)
                    .build();

            Child michael = Child.builder()
                    .firstName("Michael")
                    .lastName("Smith")
                    .dob(LocalDate.now().minusYears(2).minusMonths(3))
                    .gender(Gender.MALE)
                    .weight(11.8)
                    .height(83.5)
                    .bloodType("B-")
                    .healthNote("No allergies, good health")
                    .guardian(user)
                    .build();

            john = childrenRepository.save(john);
            emma = childrenRepository.save(emma);
            michael = childrenRepository.save(michael);

            // Create test vaccines with English descriptions
            Vaccine vaccineA = Vaccine.builder()
                    .name("Vaccine A")
                    .description("Live vaccine for basic protection")
                    .vaccineCode("VA-001")
                    .manufacturer("BioTech Inc.")
                    .price(150000.0)
                    .expiresInDays(365L)
                    .minAge(1)
                    .maxAge(10)
                    .dose(1)
                    .activated(true)  // Live vaccine
                    .build();

            Vaccine vaccineB = Vaccine.builder()
                    .name("Vaccine B")
                    .description("Inactivated vaccine for basic protection")
                    .vaccineCode("VB-001")
                    .manufacturer("MediVax Corp.")
                    .price(125000.0)
                    .expiresInDays(730L)
                    .minAge(1)
                    .maxAge(12)
                    .dose(1)
                    .activated(false)  // Inactive vaccine
                    .build();

            Vaccine vaccineC = Vaccine.builder()
                    .name("Vaccine C")
                    .description("Inactivated vaccine for advanced protection")
                    .vaccineCode("VC-001")
                    .manufacturer("ImmunoShield")
                    .price(200000.0)
                    .expiresInDays(365L)
                    .minAge(1)
                    .maxAge(15)
                    .dose(1)
                    .activated(false)  // Inactive vaccine
                    .build();

            Vaccine vaccineD = Vaccine.builder()
                    .name("Vaccine D")
                    .description("Live vaccine for advanced protection")
                    .vaccineCode("VD-001")
                    .manufacturer("VaxGuard")
                    .price(175000.0)
                    .expiresInDays(180L)
                    .minAge(1)
                    .maxAge(10)
                    .dose(1)
                    .activated(true)  // Live vaccine
                    .build();

            Vaccine vaccineX = Vaccine.builder()
                    .name("Vaccine X")
                    .description("Prerequisite condition vaccine")
                    .vaccineCode("VX-001")
                    .manufacturer("PrimeTech")
                    .price(100000.0)
                    .expiresInDays(365L)
                    .minAge(0)
                    .maxAge(20)
                    .dose(1)
                    .activated(false)  // Inactive vaccine
                    .build();

            Vaccine vaccineY = Vaccine.builder()
                    .name("Vaccine Y")
                    .description("Sequential vaccine with multiple doses")
                    .vaccineCode("VY-001")
                    .manufacturer("BioShield")
                    .price(225000.0)
                    .expiresInDays(365L)
                    .minAge(1)
                    .maxAge(18)
                    .dose(2)  // Two doses required
                    .activated(true)  // Live vaccine
                    .build();

            Vaccine vaccineZ = Vaccine.builder()
                    .name("Vaccine Z")
                    .description("Inactivated vaccine with multiple doses")
                    .vaccineCode("VZ-001")
                    .manufacturer("ImmunoTech")
                    .price(175000.0)
                    .expiresInDays(365L)
                    .minAge(1)
                    .maxAge(20)
                    .dose(3)  // Three doses required
                    .activated(false)  // Inactive vaccine
                    .build();

            Vaccine vaccineM = Vaccine.builder()
                    .name("Vaccine M")
                    .description("Live vaccine for triple protection")
                    .vaccineCode("VM-001")
                    .manufacturer("MediGuard")
                    .price(150000.0)
                    .expiresInDays(180L)
                    .minAge(1)
                    .maxAge(10)
                    .dose(1)
                    .activated(true)  // Live vaccine
                    .build();

            Vaccine vaccineN = Vaccine.builder()
                    .name("Vaccine N")
                    .description("Live vaccine for triple protection - higher age requirement")
                    .vaccineCode("VN-001")
                    .manufacturer("VaxShield")
                    .price(200000.0)
                    .expiresInDays(365L)
                    .minAge(2)  // Higher age requirement - all our children are now 2+
                    .maxAge(15)
                    .dose(1)
                    .activated(true)  // Live vaccine
                    .build();

            Vaccine vaccineP = Vaccine.builder()
                    .name("Vaccine P")
                    .description("Inactivated vaccine for triple protection capability")
                    .vaccineCode("VP-001")
                    .manufacturer("ImmunoPro")
                    .price(180000.0)
                    .expiresInDays(730L)
                    .minAge(1)
                    .maxAge(18)
                    .dose(1)
                    .activated(false)  // Inactive vaccine
                    .build();

            Vaccine vaccineQ = Vaccine.builder()
                    .name("Vaccine Q")
                    .description("Next inactivated vaccine with multiple doses")
                    .vaccineCode("VQ-001")
                    .manufacturer("BioDefend")
                    .price(250000.0)
                    .expiresInDays(365L)
                    .minAge(1)
                    .maxAge(20)
                    .dose(2)  // Two doses required
                    .activated(false)  // Inactive vaccine
                    .build();

            // Save vaccines
            vaccineA = vaccineRepository.save(vaccineA);
            vaccineB = vaccineRepository.save(vaccineB);
            vaccineC = vaccineRepository.save(vaccineC);
            vaccineD = vaccineRepository.save(vaccineD);
            vaccineX = vaccineRepository.save(vaccineX);
            vaccineY = vaccineRepository.save(vaccineY);
            vaccineZ = vaccineRepository.save(vaccineZ);
            vaccineM = vaccineRepository.save(vaccineM);
            vaccineN = vaccineRepository.save(vaccineN);
            vaccineP = vaccineRepository.save(vaccineP);
            vaccineQ = vaccineRepository.save(vaccineQ);

            // Create VaccineTimings for all vaccines
            List<VaccineTiming> vaccineTimings = new ArrayList<>();

            // Single-dose vaccines
            for (Vaccine vaccine : new Vaccine[]{vaccineA, vaccineB, vaccineC, vaccineD, vaccineM, vaccineN, vaccineP, vaccineX}) {
                VaccineTiming singleDoseTiming = VaccineTiming.builder()
                        .doseNo(1)
                        .vaccine(vaccine)
                        .intervalDays(0L)  // First (and only) dose has no interval
                        .build();
                vaccineTimings.add(singleDoseTiming);
            }

            // Multi-dose vaccines
            vaccineTimings.addAll(Arrays.asList(
                    VaccineTiming.builder()
                            .doseNo(1)
                            .vaccine(vaccineY)
                            .intervalDays(0L)  // First dose has no interval
                            .build(),
                    VaccineTiming.builder()
                            .doseNo(2)
                            .vaccine(vaccineY)
                            .intervalDays(30L)  // 30 days after first dose
                            .build(),
                    VaccineTiming.builder()
                            .doseNo(1)
                            .vaccine(vaccineZ)
                            .intervalDays(0L)  // First dose has no interval
                            .build(),
                    VaccineTiming.builder()
                            .doseNo(2)
                            .vaccine(vaccineZ)
                            .intervalDays(28L)  // 28 days after first dose
                            .build(),
                    VaccineTiming.builder()
                            .doseNo(3)
                            .vaccine(vaccineZ)
                            .intervalDays(180L)  // 180 days after second dose
                            .build(),
                    VaccineTiming.builder()
                            .doseNo(1)
                            .vaccine(vaccineQ)
                            .intervalDays(0L)  // First dose has no interval
                            .build(),
                    VaccineTiming.builder()
                            .doseNo(2)
                            .vaccine(vaccineQ)
                            .intervalDays(60L)  // 60 days after first dose
                            .build()
            ));

            // Save all vaccine timings
            vaccineTimingRepository.saveAll(vaccineTimings);

            // Vaccine Use Initialization - in English
            List<VaccineUse> vaccineUses = new ArrayList<>(List.of(
                    VaccineUse.builder()
                            .name("Childhood Immunization")
                            .description("Vaccines administered to infants and children as part of a standard immunization schedule")
                            .build(),
                    VaccineUse.builder()
                            .name("Preventive Care")
                            .description("Vaccines for preventing common childhood diseases")
                            .build(),
                    VaccineUse.builder()
                            .name("Travel Vaccination")
                            .description("Recommended vaccines for international travel")
                            .build(),
                    VaccineUse.builder()
                            .name("Seasonal Protection")
                            .description("Vaccines for seasonal disease prevention")
                            .build(),
                    VaccineUse.builder()
                            .name("Occupational Health")
                            .description("Vaccines recommended for specific professions with elevated exposure risks")
                            .build()
            ));

            // Save vaccine uses
            vaccineUses = vaccineUseRepository.saveAll(vaccineUses);

            // Associate vaccines with uses
            vaccineA.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1))));
            vaccineB.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1))));
            vaccineC.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1))));
            vaccineD.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1))));
            vaccineY.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1), vaccineUses.get(2))));
            vaccineZ.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1), vaccineUses.get(3))));
            vaccineM.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1))));
            vaccineN.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1))));
            vaccineP.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1))));
            vaccineQ.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1), vaccineUses.get(3))));
            vaccineX.setUses(new ArrayList<>(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1))));

            // Save updated vaccines with uses
            vaccineRepository.saveAll(Arrays.asList(
                    vaccineA, vaccineB, vaccineC, vaccineD, vaccineY,
                    vaccineZ, vaccineM, vaccineN, vaccineP, vaccineQ, vaccineX
            ));

            // Create specific combos in English
            Combo basicProtection = Combo.builder()
                    .name("Basic Protection")
                    .description("Basic protection combo with vaccines A and B")
                    .price(250000.0)
                    .minAge(1)
                    .maxAge(10)
                    .build();

            Combo advancedProtection = Combo.builder()
                    .name("Advanced Protection")
                    .description("Advanced protection combo with vaccines C and D")
                    .price(350000.0)
                    .minAge(1)
                    .maxAge(10)
                    .build();

            Combo tripleProtection = Combo.builder()
                    .name("Triple Layer Protection")
                    .description("Comprehensive protection with three vaccines")
                    .price(500000.0)
                    .minAge(1)
                    .maxAge(10)
                    .build();

            // Save combos
            basicProtection = comboRepository.save(basicProtection);
            advancedProtection = comboRepository.save(advancedProtection);
            tripleProtection = comboRepository.save(tripleProtection);

            // Create combo-vaccine associations
            VaccineCombo vaccineComboA = VaccineCombo.builder()
                    .id(VaccineComboId.builder()
                            .comboId(basicProtection.getId())
                            .vaccineId(vaccineA.getId())
                            .orderInCombo(1)
                            .build())
                    .combo(basicProtection)
                    .vaccine(vaccineA)
                    .intervalDays(0L)  // First vaccine in combo
                    .build();

            VaccineCombo vaccineComboB = VaccineCombo.builder()
                    .id(VaccineComboId.builder()
                            .comboId(basicProtection.getId())
                            .vaccineId(vaccineB.getId())
                            .orderInCombo(2)
                            .build())
                    .combo(basicProtection)
                    .vaccine(vaccineB)
                    .intervalDays(14L)  // 14 days after vaccine A
                    .build();

            VaccineCombo vaccineComboC = VaccineCombo.builder()
                    .id(VaccineComboId.builder()
                            .comboId(advancedProtection.getId())
                            .vaccineId(vaccineC.getId())
                            .orderInCombo(1)
                            .build())
                    .combo(advancedProtection)
                    .vaccine(vaccineC)
                    .intervalDays(0L)  // First vaccine in combo
                    .build();

            VaccineCombo vaccineComboD = VaccineCombo.builder()
                    .id(VaccineComboId.builder()
                            .comboId(advancedProtection.getId())
                            .vaccineId(vaccineD.getId())
                            .orderInCombo(2)
                            .build())
                    .combo(advancedProtection)
                    .vaccine(vaccineD)
                    .intervalDays(21L)  // 21 days after vaccine C
                    .build();

            VaccineCombo vaccineComboM = VaccineCombo.builder()
                    .id(VaccineComboId.builder()
                            .comboId(tripleProtection.getId())
                            .vaccineId(vaccineM.getId())
                            .orderInCombo(1)
                            .build())
                    .combo(tripleProtection)
                    .vaccine(vaccineM)
                    .intervalDays(0L)  // First vaccine in combo
                    .build();

            VaccineCombo vaccineComboN = VaccineCombo.builder()
                    .id(VaccineComboId.builder()
                            .comboId(tripleProtection.getId())
                            .vaccineId(vaccineN.getId())
                            .orderInCombo(2)
                            .build())
                    .combo(tripleProtection)
                    .vaccine(vaccineN)
                    .intervalDays(21L)  // 21 days after vaccine M
                    .build();

            VaccineCombo vaccineComboP = VaccineCombo.builder()
                    .id(VaccineComboId.builder()
                            .comboId(tripleProtection.getId())
                            .vaccineId(vaccineP.getId())
                            .orderInCombo(3)
                            .build())
                    .combo(tripleProtection)
                    .vaccine(vaccineP)
                    .intervalDays(21L)  // 21 days after vaccine N
                    .build();

            // Save vaccine-combo associations
            vaccineComboRepository.save(vaccineComboA);
            vaccineComboRepository.save(vaccineComboB);
            vaccineComboRepository.save(vaccineComboC);
            vaccineComboRepository.save(vaccineComboD);
            vaccineComboRepository.save(vaccineComboM);
            vaccineComboRepository.save(vaccineComboN);
            vaccineComboRepository.save(vaccineComboP);

            // Create specific vaccine intervals
            VaccineInterval intervalBtoC = VaccineInterval.builder()
                    .id(VaccineIntervalId.builder()
                            .fromVaccineId(vaccineB.getId())
                            .toVaccineId(vaccineC.getId())
                            .build())
                    .fromVaccine(vaccineB)
                    .toVaccine(vaccineC)
                    .daysBetween(30)  // 30 days required between B and C
                    .build();

            VaccineInterval intervalXtoY = VaccineInterval.builder()
                    .id(VaccineIntervalId.builder()
                            .fromVaccineId(vaccineX.getId())
                            .toVaccineId(vaccineY.getId())
                            .build())
                    .fromVaccine(vaccineX)
                    .toVaccine(vaccineY)
                    .daysBetween(60)  // 60 days required between X and Y
                    .build();

            VaccineInterval intervalPtoQ = VaccineInterval.builder()
                    .id(VaccineIntervalId.builder()
                            .fromVaccineId(vaccineP.getId())
                            .toVaccineId(vaccineQ.getId())
                            .build())
                    .fromVaccine(vaccineP)
                    .toVaccine(vaccineQ)
                    .daysBetween(45)  // 45 days required between P and Q
                    .build();

            // Save vaccine intervals
            vaccineIntervalRepository.save(intervalBtoC);
            vaccineIntervalRepository.save(intervalXtoY);
            vaccineIntervalRepository.save(intervalPtoQ);

            // Create order and schedule examples
            Order order1 = Order.builder()
                    .bookDate(LocalDate.now().minusDays(71).atTime(10, 0))
                    .startDate(LocalDate.now().minusDays(70).atTime(10, 0))
                    .serviceType(ServiceType.SINGLE)
                    .status(OrderStatus.PAID)
                    .totalPrice(vaccineX.getPrice())
                    .customer(user)
                    .child(emma)
                    .build();
            orderRepository.save(order1);

            // Create a previous vaccination record for Emma (Vaccine X)
            VaccineSchedule emmaVaccineX = VaccineSchedule.builder()
                    .date(LocalDate.now().minusDays(70).atTime(10, 0))  // 70 days ago, exceeding the 60-day requirement
                    .status(VaccineScheduleStatus.COMPLETED)
                    .vaccine(vaccineX)
                    .doctor(doctor)
                    .customer(user)
                    .child(emma)
                    .order(order1)
                    .orderNo(1)
                    .build();
            vaccineScheduleRepository.save(emmaVaccineX);

            // Create doctor's busy schedule with upcoming appointments
            LocalDateTime appointmentTime1 = LocalDate.now().plusDays(16).atTime(10, 0);
            Order order2 = Order.builder()
                    .bookDate(appointmentTime1.minusDays(1))
                    .startDate(appointmentTime1)
                    .serviceType(ServiceType.SINGLE)
                    .status(OrderStatus.PAID)
                    .totalPrice(vaccineA.getPrice())
                    .customer(user)
                    .child(john)
                    .build();
            orderRepository.save(order2);

            VaccineSchedule busySlot1 = VaccineSchedule.builder()
                    .date(appointmentTime1)
                    .status(VaccineScheduleStatus.PLANNED)
                    .vaccine(vaccineA)
                    .doctor(doctor)
                    .customer(user)
                    .child(john)
                    .orderNo(1)
                    .order(order2)
                    .build();
            vaccineScheduleRepository.save(busySlot1);

            // More busy slots for the doctor
            LocalDateTime appointmentTime2 = LocalDate.now().plusDays(30).atTime(9, 0);
            LocalDateTime appointmentTime3 = LocalDate.now().plusDays(30).atTime(10, 0);

            Order order3 = Order.builder()
                    .bookDate(appointmentTime2.minusDays(1))
                    .startDate(appointmentTime2)
                    .serviceType(ServiceType.SINGLE)
                    .status(OrderStatus.PAID)
                    .totalPrice(vaccineB.getPrice() + vaccineA.getPrice())
                    .customer(user)
                    .child(emma)
                    .build();
            orderRepository.save(order3);

            VaccineSchedule busySlot2 = VaccineSchedule.builder()
                    .date(appointmentTime2)
                    .status(VaccineScheduleStatus.PLANNED)
                    .vaccine(vaccineB)
                    .doctor(doctor)
                    .customer(user)
                    .child(emma)
                    .orderNo(1)
                    .order(order3)
                    .build();

            VaccineSchedule busySlot3 = VaccineSchedule.builder()
                    .date(appointmentTime3)
                    .status(VaccineScheduleStatus.PLANNED)
                    .vaccine(vaccineC)
                    .doctor(doctor)
                    .customer(user)
                    .child(emma)
                    .orderNo(1)
                    .order(order3)
                    .build();

            vaccineScheduleRepository.save(busySlot2);
            vaccineScheduleRepository.save(busySlot3);

            // Create batches for all vaccines to ensure they can be administered
            for (Vaccine vaccine : vaccineRepository.findAll()) {
                Batch batch = Batch.builder()
                        .batchCode("BATCH-" + vaccine.getVaccineCode())
                        .batchSize(100)
                        .quantity(100)
                        .imported(LocalDateTime.now())
                        .manufactured(LocalDateTime.now().minusMonths(1))
                        .expiration(LocalDateTime.now().plusYears(1))
                        .distributer("Standard Distributor")
                        .vaccine(vaccine)
                        .build();
                batchRepository.save(batch);
            }

            logger.info("Database successfully initialized with test data for vaccine scheduling demo.");
        } catch (Exception e) {
            logger.severe("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}