package com.vaccinex.service;

import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.base.exception.IdNotFoundException;
import com.vaccinex.base.security.JwtGenerator;
import com.vaccinex.dao.ChildrenDao;
import com.vaccinex.dao.UserDao;
import com.vaccinex.dao.VaccineComboDao;
import com.vaccinex.dao.VaccineScheduleDao;
import com.vaccinex.dto.request.ChildrenRequestDTO;
import com.vaccinex.dto.response.ChildrenResponseDTO;
import com.vaccinex.dto.response.InjectionHistoryResponse;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.ParseEnumException;
import com.vaccinex.mapper.ChildrenMapper;
import com.vaccinex.pojo.Child;
import com.vaccinex.pojo.User;
import com.vaccinex.pojo.VaccineCombo;
import com.vaccinex.pojo.VaccineSchedule;
import com.vaccinex.pojo.composite.VaccineComboId;
import com.vaccinex.pojo.enums.EnumTokenType;
import com.vaccinex.pojo.enums.Gender;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ChildrenServiceImpl implements ChildrenService {

    @Inject
    private ChildrenDao childRepository;

    @Inject
    private UserDao userRepository;

    @Inject
    private JwtGenerator jwtService;

    @Inject
    private VaccineScheduleDao vaccineScheduleRepository;

    @Inject
    private VaccineComboDao vaccineComboRepository;

    @Override
    public Child getChildById(Integer id) {
        return childRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                () -> new IdNotFoundException("Cannot find child with id: " + id)
        );
    }

    @Override
    public List<ChildrenResponseDTO> findAll() {
        List<ChildrenResponseDTO> childrenResponseDTOs = ChildrenMapper.INSTANCE.toDTOs(childRepository.findAllByDeletedIsFalse());
        for (ChildrenResponseDTO childrenResponseDTO : childrenResponseDTOs) {
            childrenResponseDTO.setInjectionHistories(getInjectionHistory(childrenResponseDTO.getId()));
        }
        return childrenResponseDTOs;
    }

    @Override
    public ChildrenResponseDTO findById(Integer childId) {
        Child child = getChildById(childId);
        ChildrenResponseDTO childrenResponseDTO = ChildrenMapper.INSTANCE.toDTO(child);
        childrenResponseDTO.setInjectionHistories(getInjectionHistory(childrenResponseDTO.getId()));
        return childrenResponseDTO;
    }

    @Override
    public ChildrenResponseDTO createChild(ChildrenRequestDTO dto, HttpServletRequest request) throws ParseEnumException {
        System.out.println("dto = " + dto);
        String token = request.getHeader("Authorization");
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        System.out.println(token + " = token");
        String email = jwtService.getEmailFromJwt(token, EnumTokenType.TOKEN);
        User customer = userRepository.getAccountByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new ElementNotFoundException("Cannot find user with email: " + email));

        if (dto.getDob() != null) {
            LocalDate now = LocalDate.now();
            LocalDate minDate = now.minusYears(18);
            if (dto.getDob().isBefore(minDate) || dto.getDob().isAfter(now)) {
                throw new BadRequestException("Ngày sinh của trẻ phải từ 0 đến 18 tuổi");
            }
        }
        Gender childGender;
        try {
            childGender = Gender.valueOf(String.valueOf(dto.getGender()));
        } catch (IllegalArgumentException e) {
            throw new ParseEnumException("Trẻ em giới tính không hợp lệ: " + dto.getGender());
        }
        Child child = ChildrenMapper.INSTANCE.toEntity(dto);
        child.setGender(childGender);
        child.setGuardian(customer);
        return ChildrenMapper.INSTANCE.toDTO(childRepository.save(child));
    }

    @Override
    public ChildrenResponseDTO update(Integer childId, ChildrenRequestDTO dto, HttpServletRequest request) {
        Child child = childRepository.findByIdAndDeletedIsFalse(childId)
                .orElseThrow(() -> new IdNotFoundException("Cannot find child with id: " + childId));
        String token = request.getHeader("Authorization");
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtService.getEmailFromJwt(token, EnumTokenType.TOKEN);
        User customer = userRepository.getAccountByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new ElementNotFoundException("Cannot find user with email: " + email));
        child.setFirstName(dto.getFirstName());
        child.setLastName(dto.getLastName());
        child.setDob(dto.getDob());
        child.setWeight(dto.getWeight());
        child.setHeight(dto.getHeight());
        child.setBloodType(dto.getBloodType());
        child.setHealthNote(dto.getHealthNote());
        try {
            child.setGender(Gender.valueOf(String.valueOf(dto.getGender())));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid child gender: " + dto.getGender());
        }
        child.setGuardian(customer);
        return ChildrenMapper.INSTANCE.toDTO(childRepository.save(child));
    }

    @Override
    public void deleteById(Integer childId) {
        Child child = childRepository.findByIdAndDeletedIsFalse(childId)
                .orElseThrow(() -> new IdNotFoundException("Cannot find child with id: " + childId));
        child.setDeleted(true);
        childRepository.save(child);
    }

    @Override
    public List<ChildrenResponseDTO> findByParentId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtService.getEmailFromJwt(token, EnumTokenType.TOKEN);
        User parent = userRepository.getAccountByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new ElementNotFoundException("Cannot find user with email: " + email));
        List<Child> children = childRepository.findAllByGuardianIdAndDeletedIsFalse(parent.getId());
        List<ChildrenResponseDTO> childrenResponseDTOs = ChildrenMapper.INSTANCE.toDTOs(children);
        for (ChildrenResponseDTO childrenResponseDTO : childrenResponseDTOs) {
            childrenResponseDTO.setInjectionHistories(getInjectionHistory(childrenResponseDTO.getId()));
        }
        return childrenResponseDTOs;
    }

    @Override
    public LocalDateTime getEarliestPossibleSchedule(Integer childId) {
        List<VaccineSchedule> vaccineSchedules = vaccineScheduleRepository.findByChildIdOrderByDateDesc(childId);
        if (vaccineSchedules.isEmpty()) {
            return LocalDateTime.now();
        }
        vaccineSchedules = vaccineSchedules.stream().filter(vs -> vs.getStatus() != VaccineScheduleStatus.CANCELLED).toList();
        VaccineSchedule lastVaccineSchedule = vaccineSchedules.getFirst();
        if (lastVaccineSchedule.getCombo() == null) {
            Long numberOfDate = lastVaccineSchedule.getVaccine().getVaccineTimings().get(lastVaccineSchedule.getOrderNo()).getIntervalDays();
            LocalDateTime validDate = lastVaccineSchedule.getDate().plusDays(numberOfDate);
            if (validDate.getHour() >= 20) {
                return validDate.plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
            }
            return validDate;
        }
        VaccineComboId vaccineComboId = VaccineComboId
                .builder()
                .orderInCombo(lastVaccineSchedule.getOrderNo())
                .comboId(lastVaccineSchedule.getCombo().getId())
                .vaccineId(lastVaccineSchedule.getVaccine().getId())
                .build();

        VaccineCombo vaccineCombo = vaccineComboRepository.findById(vaccineComboId).orElseThrow(() -> new IdNotFoundException("Cannot find VaccineCombo with id: " + vaccineComboId));
        Long numberOfDate = vaccineCombo.getIntervalDays();
        LocalDateTime validDate = lastVaccineSchedule.getDate().plusDays(numberOfDate);
        if (validDate.getHour() >= 20) {
            return validDate.plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        }
        return validDate;
    }

    private List<InjectionHistoryResponse> getInjectionHistory(Integer id) {
        Child child = childRepository.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new IdNotFoundException("Cannot found child with id: " + id));
        List<VaccineSchedule> schedules = child.getSchedules();

        LocalDateTime now = LocalDateTime.now();

        return schedules.stream()
                .map(schedule -> InjectionHistoryResponse.builder()
                        .id(schedule.getId())
                        .dateTime(schedule.getDate())
                        .vaccine(schedule.getVaccine() != null ? schedule.getVaccine().getName() : "N/A")
                        .status(schedule.getStatus() != null ? schedule.getStatus().name() : "UNKNOWN")
                        .build())
                .sorted((a, b) -> {
                    boolean aIsFuture = a.getDateTime().isAfter(now);
                    boolean bIsFuture = b.getDateTime().isAfter(now);

                    if (aIsFuture && bIsFuture) {
                        return a.getDateTime().compareTo(b.getDateTime());
                    } else if (!aIsFuture && !bIsFuture) {
                        return b.getDateTime().compareTo(a.getDateTime());
                    } else {
                        return aIsFuture ? -1 : 1;
                    }
                })
                .collect(Collectors.toList());
    }
}
