//package com.sba301.vaccinex.initdb;
//
//import com.sba301.vaccinex.pojo.*;
//import com.sba301.vaccinex.pojo.composite.VaccineComboId;
//import com.sba301.vaccinex.pojo.composite.VaccineIntervalId;
//import com.sba301.vaccinex.pojo.enums.*;
//import com.sba301.vaccinex.repository.*;
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DatabaseInitializer {
//
//    private final VaccineRepository vaccineRepository;
//    private final VaccineComboRepository vaccineComboRepository;
//    private final ComboRepository comboRepository;
//    private final VaccineUseRepository vaccineUseRepository;
//    private final VaccineTimingRepository vaccineTimingRepository;
//    private final RoleRepository roleRepository;
//    private final UserRepository userRepository;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    private final ChildrenRepository childrenRepository;
//    private final BatchRepository batchRepository;
//    private final VaccineIntervalRepository vaccineIntervalRepository;
//    private final VaccineScheduleRepository vaccineScheduleRepository;
//
//    @Bean
//    @Transactional
//    public CommandLineRunner initDatabase(OrderRepository orderRepository) {
//        return args -> {
//            if (roleRepository.count() > 0) {
//                return;
//            }
//
//            // Initialize standard roles
//            Role roleAdmin = new Role(null, EnumRoleNameType.ROLE_ADMIN, "ADMIN", null);
//            Role roleDoctor = new Role(null, EnumRoleNameType.ROLE_DOCTOR, "DOCTOR", null);
//            Role roleUser = new Role(null, EnumRoleNameType.ROLE_USER, "USER", null);
//            Role roleStaff = new Role(null, EnumRoleNameType.ROLE_STAFF, "STAFF", null);
//
//            roleRepository.save(roleAdmin);
//            roleRepository.save(roleDoctor);
//            roleRepository.save(roleUser);
//            roleRepository.save(roleStaff);
//
//            // Initialize standard users
//            User user = User.builder()
//                    .firstName("Nguyên")
//                    .lastName("Mộc")
//                    .email("user@gmail.com")
//                    .phone("+84867962434")
//                    .password(bCryptPasswordEncoder.encode("123456"))
//                    .enabled(true)
//                    .address("9 Đường s")
//                    .accessToken(null)
//                    .refreshToken(null)
//                    .nonLocked(true)
//                    .role(roleUser)
//                    .build();
//            userRepository.save(user);
//
//            User admin = User.builder()
//                    .firstName("Quản lý")
//                    .lastName("")
//                    .accessToken(null)
//                    .refreshToken(null)
//                    .email("admin@gmail.com")
//                    .password(bCryptPasswordEncoder.encode("123456"))
//                    .enabled(true)
//                    .nonLocked(true)
//                    .role(roleAdmin)
//                    .build();
//            userRepository.save(admin);
//
//            User doctor = User.builder()
//                    .firstName("Đắc Phu")
//                    .lastName("Trần")
//                    .accessToken(null)
//                    .refreshToken(null)
//                    .email("trandacphu@gmail.com")
//                    .password(bCryptPasswordEncoder.encode("123456"))
//                    .enabled(true)
//                    .nonLocked(true)
//                    .role(roleDoctor)
//                    .build();
//            userRepository.save(doctor);
//
//            User doctor2 = User.builder()
//                    .firstName("Thị Chính")
//                    .lastName("Bạch")
//                    .accessToken(null)
//                    .refreshToken(null)
//                    .email("bachthichinh@gmail.com")
//                    .password(bCryptPasswordEncoder.encode("123456"))
//                    .enabled(true)
//                    .nonLocked(true)
//                    .role(roleDoctor)
//                    .build();
//            userRepository.save(doctor2);
//
//            User staff = User.builder()
//                    .firstName("Lộc")
//                    .lastName("Phạm Tấn")
//                    .accessToken(null)
//                    .refreshToken(null)
//                    .email("phamtanloc@gmail.com")
//                    .password(bCryptPasswordEncoder.encode("123456"))
//                    .enabled(true)
//                    .nonLocked(true)
//                    .role(roleStaff)
//                    .build();
//            userRepository.save(staff);
//
//            // Tạo dữ liệu thử nghiệm cho các bé với độ tuổi phù hợp để tiêm chủng
//            Child john = Child.builder()
//                    .firstName("Minh")
//                    .lastName("Nguyễn")
//                    .dob(LocalDate.now().minusYears(2).minusMonths(1))  // Hơn 2 tuổi một chút
//                    .gender(Gender.MALE)
//                    .weight(12.5)
//                    .height(86.0)
//                    .bloodType("A+")
//                    .healthNote("Bé khỏe mạnh, không bệnh nền")
//                    .guardian(user)
//                    .build();
//
//            Child emma = Child.builder()
//                    .firstName("Linh")
//                    .lastName("Trần")
//                    .dob(LocalDate.now().minusYears(2))  // Đủ 2 tuổi
//                    .gender(Gender.FEMALE)
//                    .weight(10.2)
//                    .height(76.5)
//                    .bloodType("O+")
//                    .healthNote("Bé có tiền sử viêm da cơ địa nhẹ")
//                    .guardian(user)
//                    .build();
//
//            Child michael = Child.builder()
//                    .firstName("Hiếu")
//                    .lastName("Lê")
//                    .dob(LocalDate.now().minusYears(2).minusMonths(3))  // 2 tuổi 3 tháng
//                    .gender(Gender.MALE)
//                    .weight(11.8)
//                    .height(83.5)
//                    .bloodType("B-")
//                    .healthNote("Không dị ứng, sức khỏe tốt")
//                    .guardian(user)
//                    .build();
//
//            john = childrenRepository.save(john);
//            emma = childrenRepository.save(emma);
//            michael = childrenRepository.save(michael);
//
//            // Create test vaccines for our scenarios
//            Vaccine vaccineA = Vaccine.builder()
//                    .name("Vaccine A")
//                    .description("Vắc xin sống để bảo vệ cơ bản")
//                    .vaccineCode("VA-001")
//                    .manufacturer("BioTech Inc.")
//                    .price(150000.0)
//                    .expiresInDays(365L)
//                    .minAge(1)
//                    .maxAge(10)
//                    .dose(1)
//                    .activated(true)  // Live vaccine
//                    .build();
//
//            Vaccine vaccineB = Vaccine.builder()
//                    .name("Vaccine B")
//                    .description("Vắc xin bất hoạt để bảo vệ cơ bản")
//                    .vaccineCode("VB-001")
//                    .manufacturer("MediVax Corp.")
//                    .price(125000.0)
//                    .expiresInDays(730L)
//                    .minAge(1)
//                    .maxAge(12)
//                    .dose(1)
//                    .activated(false)  // Inactive vaccine
//                    .build();
//
//            Vaccine vaccineC = Vaccine.builder()
//                    .name("Vaccine C")
//                    .description("Vắc xin bất hoạt để bảo vệ nâng cao")
//                    .vaccineCode("VC-001")
//                    .manufacturer("ImmunoShield")
//                    .price(200000.0)
//                    .expiresInDays(365L)
//                    .minAge(1)
//                    .maxAge(15)
//                    .dose(1)
//                    .activated(false)  // Inactive vaccine
//                    .build();
//
//            Vaccine vaccineD = Vaccine.builder()
//                    .name("Vaccine D")
//                    .description("Vắc xin sống để bảo vệ nâng cao")
//                    .vaccineCode("VD-001")
//                    .manufacturer("VaxGuard")
//                    .price(175000.0)
//                    .expiresInDays(180L)
//                    .minAge(1)
//                    .maxAge(10)
//                    .dose(1)
//                    .activated(true)  // Live vaccine
//                    .build();
//
//            Vaccine vaccineX = Vaccine.builder()
//                    .name("Vaccine X")
//                    .description("Vắc xin điều kiện tiên quyết")
//                    .vaccineCode("VX-001")
//                    .manufacturer("PrimeTech")
//                    .price(100000.0)
//                    .expiresInDays(365L)
//                    .minAge(0)
//                    .maxAge(20)
//                    .dose(1)
//                    .activated(false)  // Inactive vaccine
//                    .build();
//
//            Vaccine vaccineY = Vaccine.builder()
//                    .name("Vaccine Y")
//                    .description("Vắc xin theo trình tự với nhiều liều")
//                    .vaccineCode("VY-001")
//                    .manufacturer("BioShield")
//                    .price(225000.0)
//                    .expiresInDays(365L)
//                    .minAge(1)
//                    .maxAge(18)
//                    .dose(2)  // Two doses required
//                    .activated(true)  // Live vaccine
//                    .build();
//
//            Vaccine vaccineZ = Vaccine.builder()
//                    .name("Vaccine Z")
//                    .description("Vắc-xin bất hoạt nhiều liều")
//                    .vaccineCode("VZ-001")
//                    .manufacturer("ImmunoTech")
//                    .price(175000.0)
//                    .expiresInDays(365L)
//                    .minAge(1)
//                    .maxAge(20)
//                    .dose(3)  // Three doses required
//                    .activated(false)  // Inactive vaccine
//                    .build();
//
//            Vaccine vaccineM = Vaccine.builder()
//                    .name("Vaccine M")
//                    .description("Vắc-xin sống bảo vệ ba lần")
//                    .vaccineCode("VM-001")
//                    .manufacturer("MediGuard")
//                    .price(150000.0)
//                    .expiresInDays(180L)
//                    .minAge(1)
//                    .maxAge(10)
//                    .dose(1)
//                    .activated(true)  // Live vaccine
//                    .build();
//
//            Vaccine vaccineN = Vaccine.builder()
//                    .name("Vaccine N")
//                    .description("Vắc-xin sống bảo vệ ba lần - yêu cầu độ tuổi cao hơn")
//                    .vaccineCode("VN-001")
//                    .manufacturer("VaxShield")
//                    .price(200000.0)
//                    .expiresInDays(365L)
//                    .minAge(2)  // Higher age requirement - all our children are now 2+
//                    .maxAge(15)
//                    .dose(1)
//                    .activated(true)  // Live vaccine
//                    .build();
//
//            Vaccine vaccineP = Vaccine.builder()
//                    .name("Vaccine P")
//                    .description("Vắc-xin bất hoạt cho khả năng bảo vệ ba lần")
//                    .vaccineCode("VP-001")
//                    .manufacturer("ImmunoPro")
//                    .price(180000.0)
//                    .expiresInDays(730L)
//                    .minAge(1)
//                    .maxAge(18)
//                    .dose(1)
//                    .activated(false)  // Inactive vaccine
//                    .build();
//
//            Vaccine vaccineQ = Vaccine.builder()
//                    .name("Vaccine Q")
//                    .description("Vắc-xin bất hoạt tiếp theo với nhiều liều")
//                    .vaccineCode("VQ-001")
//                    .manufacturer("BioDefend")
//                    .price(250000.0)
//                    .expiresInDays(365L)
//                    .minAge(1)
//                    .maxAge(20)
//                    .dose(2)  // Two doses required
//                    .activated(false)  // Inactive vaccine
//                    .build();
//
//            // Save vaccines
//            vaccineA = vaccineRepository.save(vaccineA);
//            vaccineB = vaccineRepository.save(vaccineB);
//            vaccineC = vaccineRepository.save(vaccineC);
//            vaccineD = vaccineRepository.save(vaccineD);
//            vaccineX = vaccineRepository.save(vaccineX);
//            vaccineY = vaccineRepository.save(vaccineY);
//            vaccineZ = vaccineRepository.save(vaccineZ);
//            vaccineM = vaccineRepository.save(vaccineM);
//            vaccineN = vaccineRepository.save(vaccineN);
//            vaccineP = vaccineRepository.save(vaccineP);
//            vaccineQ = vaccineRepository.save(vaccineQ);
//
//            // Create VaccineTimings for all vaccines
//            List<VaccineTiming> vaccineTimings = new ArrayList<>();
//
//            // Single-dose vaccines
//            for (Vaccine vaccine : new Vaccine[]{vaccineA, vaccineB, vaccineC, vaccineD, vaccineM, vaccineN, vaccineP, vaccineX}) {
//                VaccineTiming singleDoseTiming = VaccineTiming.builder()
//                        .doseNo(1)
//                        .vaccine(vaccine)
//                        .intervalDays(0L)  // First (and only) dose has no interval
//                        .build();
//                vaccineTimings.add(singleDoseTiming);
//            }
//
//            // Multi-dose vaccines (with existing logic)
//            vaccineTimings.addAll(Arrays.asList(
//                    VaccineTiming.builder()
//                            .doseNo(1)
//                            .vaccine(vaccineY)
//                            .intervalDays(0L)  // First dose has no interval
//                            .build(),
//                    VaccineTiming.builder()
//                            .doseNo(2)
//                            .vaccine(vaccineY)
//                            .intervalDays(30L)  // 30 days after first dose
//                            .build(),
//                    VaccineTiming.builder()
//                            .doseNo(1)
//                            .vaccine(vaccineZ)
//                            .intervalDays(0L)  // First dose has no interval
//                            .build(),
//                    VaccineTiming.builder()
//                            .doseNo(2)
//                            .vaccine(vaccineZ)
//                            .intervalDays(28L)  // 28 days after first dose
//                            .build(),
//                    VaccineTiming.builder()
//                            .doseNo(3)
//                            .vaccine(vaccineZ)
//                            .intervalDays(180L)  // 180 days after second dose
//                            .build(),
//                    VaccineTiming.builder()
//                            .doseNo(1)
//                            .vaccine(vaccineQ)
//                            .intervalDays(0L)  // First dose has no interval
//                            .build(),
//                    VaccineTiming.builder()
//                            .doseNo(2)
//                            .vaccine(vaccineQ)
//                            .intervalDays(60L)  // 60 days after first dose
//                            .build()
//            ));
//
//            // Save all vaccine timings
//            vaccineTimingRepository.saveAll(vaccineTimings);
//
//            // Vaccine Use Initialization
//            List<VaccineUse> vaccineUses = Arrays.asList(
//                    VaccineUse.builder()
//                            .name("Tiêm chủng cho trẻ em")
//                            .description("Vắc-xin tiêm chủng thường xuyên cho trẻ em")
//                            .build(),
//                    VaccineUse.builder()
//                            .name("Chăm sóc phòng ngừa")
//                            .description("Vắc-xin phòng ngừa các bệnh thường gặp ở trẻ em")
//                            .build(),
//                    VaccineUse.builder()
//                            .name("Tiêm chủng du lịch")
//                            .description("Vắc-xin được khuyến cáo cho du lịch quốc tế")
//                            .build(),
//                    VaccineUse.builder()
//                            .name("Bảo vệ theo mùa")
//                            .description("Vắc-xin phòng ngừa bệnh theo mùa")
//                            .build()
//            );
//
//            // Save vaccine uses
//            vaccineUses = vaccineUseRepository.saveAll(vaccineUses);
//
//            // Associate vaccines with uses
//            vaccineA.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1)));
//            vaccineB.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1)));
//            vaccineC.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1)));
//            vaccineD.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1)));
//            vaccineY.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1), vaccineUses.get(2)));
//            vaccineZ.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1), vaccineUses.get(3)));
//            vaccineM.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1)));
//            vaccineN.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1)));
//            vaccineP.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1)));
//            vaccineQ.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1), vaccineUses.get(3)));
//            vaccineX.setUses(Arrays.asList(vaccineUses.get(0), vaccineUses.get(1)));
//
//            // Save updated vaccines with uses
//            vaccineRepository.saveAll(Arrays.asList(
//                    vaccineA, vaccineB, vaccineC, vaccineD, vaccineY,
//                    vaccineZ, vaccineM, vaccineN, vaccineP, vaccineQ, vaccineX
//            ));
//
//
//            // Create specific combos
//            Combo basicProtection = Combo.builder()
//                    .name("Bảo vệ cơ bản")
//                    .description("Combo bảo vệ cơ bản với vắc-xin A và B")
//                    .price(250000.0)
//                    .minAge(1)
//                    .maxAge(10)
//                    .build();
//
//            Combo advancedProtection = Combo.builder()
//                    .name("Bảo vệ nâng cao")
//                    .description("Combo bảo vệ nâng cao với vắc-xin C và D")
//                    .price(350000.0)
//                    .minAge(1)
//                    .maxAge(10)
//                    .build();
//
//            Combo tripleProtection = Combo.builder()
//                    .name("Bảo vệ ba lớp")
//                    .description("Bảo vệ toàn diện với ba loại vắc-xin")
//                    .price(500000.0)
//                    .minAge(1)
//                    .maxAge(10)
//                    .build();
//
//            // Save combos
//            basicProtection = comboRepository.save(basicProtection);
//            advancedProtection = comboRepository.save(advancedProtection);
//            tripleProtection = comboRepository.save(tripleProtection);
//
//            // Create combo-vaccine associations
//            VaccineCombo vaccineComboA = VaccineCombo.builder()
//                    .id(VaccineComboId.builder()
//                            .comboId(basicProtection.getId())
//                            .vaccineId(vaccineA.getId())
//                            .orderInCombo(1)
//                            .build())
//                    .combo(basicProtection)
//                    .vaccine(vaccineA)
//                    .intervalDays(0L)  // First vaccine in combo
//                    .build();
//
//            VaccineCombo vaccineComboB = VaccineCombo.builder()
//                    .id(VaccineComboId.builder()
//                            .comboId(basicProtection.getId())
//                            .vaccineId(vaccineB.getId())
//                            .orderInCombo(2)
//                            .build())
//                    .combo(basicProtection)
//                    .vaccine(vaccineB)
//                    .intervalDays(14L)  // 14 days after vaccine A
//                    .build();
//
//            VaccineCombo vaccineComboC = VaccineCombo.builder()
//                    .id(VaccineComboId.builder()
//                            .comboId(advancedProtection.getId())
//                            .vaccineId(vaccineC.getId())
//                            .orderInCombo(1)
//                            .build())
//                    .combo(advancedProtection)
//                    .vaccine(vaccineC)
//                    .intervalDays(0L)  // First vaccine in combo
//                    .build();
//
//            VaccineCombo vaccineComboD = VaccineCombo.builder()
//                    .id(VaccineComboId.builder()
//                            .comboId(advancedProtection.getId())
//                            .vaccineId(vaccineD.getId())
//                            .orderInCombo(2)
//                            .build())
//                    .combo(advancedProtection)
//                    .vaccine(vaccineD)
//                    .intervalDays(21L)  // 21 days after vaccine C
//                    .build();
//
//            VaccineCombo vaccineComboM = VaccineCombo.builder()
//                    .id(VaccineComboId.builder()
//                            .comboId(tripleProtection.getId())
//                            .vaccineId(vaccineM.getId())
//                            .orderInCombo(1)
//                            .build())
//                    .combo(tripleProtection)
//                    .vaccine(vaccineM)
//                    .intervalDays(0L)  // First vaccine in combo
//                    .build();
//
//            VaccineCombo vaccineComboN = VaccineCombo.builder()
//                    .id(VaccineComboId.builder()
//                            .comboId(tripleProtection.getId())
//                            .vaccineId(vaccineN.getId())
//                            .orderInCombo(2)
//                            .build())
//                    .combo(tripleProtection)
//                    .vaccine(vaccineN)
//                    .intervalDays(21L)  // 21 days after vaccine M
//                    .build();
//
//            VaccineCombo vaccineComboP = VaccineCombo.builder()
//                    .id(VaccineComboId.builder()
//                            .comboId(tripleProtection.getId())
//                            .vaccineId(vaccineP.getId())
//                            .orderInCombo(3)
//                            .build())
//                    .combo(tripleProtection)
//                    .vaccine(vaccineP)
//                    .intervalDays(21L)  // 21 days after vaccine N
//                    .build();
//
//            // Save vaccine-combo associations
//            vaccineComboRepository.save(vaccineComboA);
//            vaccineComboRepository.save(vaccineComboB);
//            vaccineComboRepository.save(vaccineComboC);
//            vaccineComboRepository.save(vaccineComboD);
//            vaccineComboRepository.save(vaccineComboM);
//            vaccineComboRepository.save(vaccineComboN);
//            vaccineComboRepository.save(vaccineComboP);
//
//            // Create specific vaccine intervals
//            VaccineInterval intervalBtoC = VaccineInterval.builder()
//                    .id(VaccineIntervalId.builder()
//                            .fromVaccineId(vaccineB.getId())
//                            .toVaccineId(vaccineC.getId())
//                            .build())
//                    .fromVaccine(vaccineB)
//                    .toVaccine(vaccineC)
//                    .daysBetween(30)  // 30 days required between B and C
//                    .build();
//
//            VaccineInterval intervalXtoY = VaccineInterval.builder()
//                    .id(VaccineIntervalId.builder()
//                            .fromVaccineId(vaccineX.getId())
//                            .toVaccineId(vaccineY.getId())
//                            .build())
//                    .fromVaccine(vaccineX)
//                    .toVaccine(vaccineY)
//                    .daysBetween(60)  // 60 days required between X and Y
//                    .build();
//
//            VaccineInterval intervalPtoQ = VaccineInterval.builder()
//                    .id(VaccineIntervalId.builder()
//                            .fromVaccineId(vaccineP.getId())
//                            .toVaccineId(vaccineQ.getId())
//                            .build())
//                    .fromVaccine(vaccineP)
//                    .toVaccine(vaccineQ)
//                    .daysBetween(45)  // 45 days required between P and Q
//                    .build();
//
//            // Save vaccine intervals
//            vaccineIntervalRepository.save(intervalBtoC);
//            vaccineIntervalRepository.save(intervalXtoY);
//            vaccineIntervalRepository.save(intervalPtoQ);
//
//            Order order1 = Order.builder()
//                    .bookDate(LocalDate.now().minusDays(71).atTime(10, 0))
//                    .startDate(LocalDate.now().minusDays(70).atTime(10, 0))
//                    .serviceType(ServiceType.SINGLE)
//                    .status(OrderStatus.PAID)
//                    .totalPrice(vaccineX.getPrice())
//                    .customer(user)
//                    .child(emma)
//                    .build();
//            orderRepository.save(order1);
//            // Create a previous vaccination record for Emma (Vaccine X)
//            VaccineSchedule emmaVaccineX = VaccineSchedule.builder()
//                    .date(LocalDate.now().minusDays(70).atTime(10, 0))  // 70 days ago, exceeding the 60-day requirement
//                    .status(VaccineScheduleStatus.COMPLETED)
//                    .vaccine(vaccineX)
//                    .doctor(doctor)
//                    .customer(user)
//                    .child(emma)
//                    .order(order1)
//                    .orderNo(1)
//                    .build();
//            vaccineScheduleRepository.save(emmaVaccineX);
//
//            // Create doctor's busy schedule
//            // For Dr. Jones - create appointments with relative dates
//
//            LocalDateTime appointmentTime1 = LocalDate.now().plusDays(16).atTime(10, 0);
//            Order order2 = Order.builder()
//                    .bookDate(appointmentTime1.minusDays(1))
//                    .startDate(appointmentTime1)
//                    .serviceType(ServiceType.SINGLE)
//                    .status(OrderStatus.PAID)
//                    .totalPrice(vaccineA.getPrice())
//                    .customer(user)
//                    .child(john)
//                    .build();
//            orderRepository.save(order2);
//            VaccineSchedule busySlot1 = VaccineSchedule.builder()
//                    .date(appointmentTime1)
//                    .status(VaccineScheduleStatus.PLANNED)
//                    .vaccine(vaccineA)
//                    .doctor(doctor)
//                    .customer(user)
//                    .child(john)
//                    .orderNo(1)
//                    .order(order2)
//                    .build();
//            vaccineScheduleRepository.save(busySlot1);
//
//            // More busy slots for the doctor
//
//            LocalDateTime appointmentTime2 = LocalDate.now().plusDays(30).atTime(9, 0);
//            LocalDateTime appointmentTime3 = LocalDate.now().plusDays(30).atTime(10, 0);
//
//            Order order3 = Order.builder()
//                    .bookDate(appointmentTime2.minusDays(1))
//                    .startDate(appointmentTime2)
//                    .serviceType(ServiceType.SINGLE)
//                    .status(OrderStatus.PAID)
//                    .totalPrice(vaccineB.getPrice() + vaccineA.getPrice())
//                    .customer(user)
//                    .child(emma)
//                    .build();
//            orderRepository.save(order3);
//
//            VaccineSchedule busySlot2 = VaccineSchedule.builder()
//                    .date(appointmentTime2)
//                    .status(VaccineScheduleStatus.PLANNED)
//                    .vaccine(vaccineB)
//                    .doctor(doctor)
//                    .customer(user)
//                    .child(emma)
//                    .orderNo(1)
//                    .order(order3)
//                    .build();
//
//            VaccineSchedule busySlot3 = VaccineSchedule.builder()
//                    .date(appointmentTime3)
//                    .status(VaccineScheduleStatus.PLANNED)
//                    .vaccine(vaccineC)
//                    .doctor(doctor)
//                    .customer(user)
//                    .child(emma)
//                    .orderNo(1)
//                    .order(order3)
//                    .build();
//
//            vaccineScheduleRepository.save(busySlot2);
//            vaccineScheduleRepository.save(busySlot3);
//
//            // Create batches for all vaccines to ensure they can be administered
//            for (Vaccine vaccine : vaccineRepository.findAll()) {
//                Batch batch = Batch.builder()
//                        .batchCode("BATCH-" + vaccine.getVaccineCode())
//                        .batchSize(100)
//                        .quantity(100)
//                        .imported(LocalDateTime.now())
//                        .manufactured(LocalDateTime.now().minusMonths(1))
//                        .expiration(LocalDateTime.now().plusYears(1))
//                        .distributer("Nhà phân phối tiêu chuẩn")
//                        .vaccine(vaccine)
//                        .build();
//                batchRepository.save(batch);
//            }
//
//            log.info("Cơ sở dữ liệu đã được khởi tạo thành công với dữ liệu thử nghiệm để trình diễn lịch trình tiêm vắc-xin.");
//        };
//    }
//}