package com.vaccinex.service;

import com.vaccinex.base.config.AppConfig;
import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dao.ComboDao;
import com.vaccinex.dto.request.VaccineComboCreateRequest;
import com.vaccinex.dto.request.VaccineComboUpdateRequest;
import com.vaccinex.dto.response.ComboResponseDTO;
import com.vaccinex.dto.response.VaccineComboResponseDTO;
import com.vaccinex.mapper.ComboMapper;
import com.vaccinex.mapper.VaccineMapper;
import com.vaccinex.pojo.Combo;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineCombo;
import com.vaccinex.pojo.VaccineTiming;
import com.vaccinex.pojo.composite.VaccineComboId;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ComboServiceImpl extends BaseServiceImpl<Combo, Integer> implements ComboService {

    @Inject
    private ComboDao comboRepository;

    @Inject
    private ComboMapper comboMapper;

    @Inject
    private VaccineMapper vaccineMapper;

    public ComboServiceImpl() {
        super(Combo.class);
    }

    private int getBusinessIntervalAfterActiveVaccine() {
        return AppConfig.getBusinessIntervalAfterActiveVaccine();
    }

    private int getBusinessIntervalAfterInactiveVaccine() {
        return AppConfig.getBusinessIntervalAfterInactiveVaccine();
    }

    @Override
    public ComboResponseDTO toComboResponseDTO(Combo combo) {
        return comboMapper.comboToComboResponseDTO(combo);
    }

    @Override
    public Combo getComboById(Integer id) {
        return comboRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new ElementNotFoundException("Combo not found with id: " + id));
    }

    @Override
    public List<ComboResponseDTO> getAllCombos() {
        List<Combo> combos = comboRepository.findAll();
        return combos.stream()
                .map(this::toComboResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComboResponseDTO> getAllCombosActive() {
        List<Combo> activeCombos = comboRepository.findAllByDeletedIsFalse();
        return activeCombos.stream()
                .map(this::toComboResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComboResponseDTO> searchVaccineCombos(String name, String price, Integer minAge, Integer maxAge) {
        List<Combo> allCombos = comboRepository.findAllByDeletedIsFalse();

        // Filter by name if provided
        if (name != null && !name.isEmpty()) {
            allCombos = allCombos.stream()
                    .filter(combo -> combo.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filter by price category if provided
        if (price != null && !price.isEmpty()) {
            Double priceBegin = null;
            Double priceEnd = null;

            switch (price.trim().toLowerCase()) {
                case "low":
                    priceEnd = Double.parseDouble(AppConfig.getPriceComboBelow());
                    break;
                case "high":
                    priceBegin = Double.parseDouble(AppConfig.getPriceComboHigher());
                    break;
                case "medium":
                    priceBegin = Double.parseDouble(AppConfig.getPriceComboAvgBegin());
                    priceEnd = Double.parseDouble(AppConfig.getPriceComboAvgEnd());
                    break;
            }

            final Double finalPriceBegin = priceBegin;
            final Double finalPriceEnd = priceEnd;

            if (finalPriceBegin != null) {
                allCombos = allCombos.stream()
                        .filter(combo -> combo.getPrice() >= finalPriceBegin)
                        .collect(Collectors.toList());
            }

            if (finalPriceEnd != null) {
                allCombos = allCombos.stream()
                        .filter(combo -> combo.getPrice() <= finalPriceEnd)
                        .collect(Collectors.toList());
            }
        }

        // Filter by min age if provided
        if (minAge != null && minAge > 0) {
            allCombos = allCombos.stream()
                    .filter(combo -> combo.getMinAge() >= minAge)
                    .collect(Collectors.toList());
        }

        // Filter by max age if provided
        if (maxAge != null && maxAge > 0) {
            allCombos = allCombos.stream()
                    .filter(combo -> combo.getMaxAge() <= maxAge)
                    .collect(Collectors.toList());
        }

        return allCombos.stream()
                .map(this::toComboResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ComboResponseDTO createCombo(VaccineComboCreateRequest vaccineComboCreateRequest) {
        Combo checkExist = comboRepository.findComboByName(vaccineComboCreateRequest.getName());
        if (checkExist != null) {
            throw new ElementExistException("A combo with this name already exists: " + vaccineComboCreateRequest.getName());
        }

        if (vaccineComboCreateRequest.getMinAge() > vaccineComboCreateRequest.getMaxAge()) {
            throw new BadRequestException("Minimum age must be less than maximum age");
        }

        if (vaccineComboCreateRequest.getVaccines() == null || vaccineComboCreateRequest.getVaccines().isEmpty()) {
            throw new BadRequestException("Combo must contain at least one vaccine");
        }

        Combo combo = Combo.builder()
                .name(vaccineComboCreateRequest.getName())
                .description(vaccineComboCreateRequest.getDescription())
                .minAge(vaccineComboCreateRequest.getMinAge())
                .maxAge(vaccineComboCreateRequest.getMaxAge())
                .price(vaccineComboCreateRequest.getPrice())
                .build();

        int comboId = comboRepository.save(combo).getId();

        List<VaccineCombo> updatedVaccineCombo = new ArrayList<>();

        VaccineComboResponseDTO previousVaccineCombo = null;

        for (VaccineComboResponseDTO vaccineComboResponseDTO : vaccineComboCreateRequest.getVaccines()) {

            Vaccine vaccine = vaccineMapper.vaccineResponseDTOToVaccine(vaccineComboResponseDTO.getVaccine());

            VaccineComboId vaccineComboId = new VaccineComboId(vaccineComboResponseDTO.getId().getVaccineId(), comboId, vaccineComboResponseDTO.getId().getOrderInCombo());

            if (previousVaccineCombo != null) {
                boolean isCurrentActivated = previousVaccineCombo.getVaccine().isActivated();
                int requiredInterval = isCurrentActivated ? getBusinessIntervalAfterActiveVaccine() : getBusinessIntervalAfterInactiveVaccine();
                long actualInterval = vaccineComboResponseDTO.getIntervalDays();

                if (actualInterval < requiredInterval) {
                    throw new BadRequestException(vaccine.getName() + " must be separated from " + previousVaccineCombo.getVaccine().getName() + " by at least " + requiredInterval + " days");
                }
            }

            VaccineCombo vaccineCombo = VaccineCombo.builder()
                    .id(vaccineComboId)
                    .combo(combo)
                    .vaccine(vaccine)
                    .intervalDays(vaccineComboResponseDTO.getIntervalDays())
                    .build();
            updatedVaccineCombo.add(vaccineCombo);

            previousVaccineCombo = vaccineComboResponseDTO;
        }
        combo.setVaccineCombos(updatedVaccineCombo);

        return toComboResponseDTO(comboRepository.save(combo));
    }

    @Transactional
    @Override
    public ComboResponseDTO updateCombo(VaccineComboUpdateRequest vaccineComboUpdateRequest, int comboID) {
        Combo combo = comboRepository.findById(comboID)
                .orElseThrow(() -> new ElementNotFoundException("Combo not found with id: " + comboID));

        if (vaccineComboUpdateRequest.getName() != null) {
            if (!combo.getName().equals(vaccineComboUpdateRequest.getName())) {
                Combo checkExist = comboRepository.findComboByName(vaccineComboUpdateRequest.getName());
                if (checkExist != null) {
                    throw new ElementExistException("A combo with this name already exists: " + vaccineComboUpdateRequest.getName());
                }
            }
            combo.setName(vaccineComboUpdateRequest.getName());
        }
        if (vaccineComboUpdateRequest.getDescription() != null) {
            combo.setDescription(vaccineComboUpdateRequest.getDescription());
        }
        if (vaccineComboUpdateRequest.getPrice() != null) {
            combo.setPrice(vaccineComboUpdateRequest.getPrice());
        }
        if (vaccineComboUpdateRequest.getMinAge() != null) {
            combo.setMinAge(vaccineComboUpdateRequest.getMinAge());
        }
        if (vaccineComboUpdateRequest.getMaxAge() != null) {
            combo.setMaxAge(vaccineComboUpdateRequest.getMaxAge());
        }

        if (vaccineComboUpdateRequest.getVaccines() != null && !vaccineComboUpdateRequest.getVaccines().isEmpty()) {

            List<VaccineCombo> updatedVaccineCombo = new ArrayList<>();

            VaccineComboResponseDTO previousVaccineCombo = null;

            int check = 1;

            for (VaccineComboResponseDTO vaccineComboResponseDTO : vaccineComboUpdateRequest.getVaccines()) {

                Vaccine vaccine = vaccineMapper.vaccineResponseDTOToVaccine(vaccineComboResponseDTO.getVaccine());

                VaccineComboId vaccineComboId = new VaccineComboId(vaccineComboResponseDTO.getId().getVaccineId(), comboID, vaccineComboResponseDTO.getId().getOrderInCombo());

                if (previousVaccineCombo != null) {

                    if (vaccine.equals(vaccineMapper.vaccineResponseDTOToVaccine(previousVaccineCombo.getVaccine()))) {
                        check += 1;
                        for (VaccineTiming vaccineTiming : vaccine.getVaccineTimings()) {
                            if (vaccineTiming.getDoseNo() == check) {
                                if (vaccineTiming.getIntervalDays() != vaccineComboResponseDTO.getIntervalDays()) {
                                    throw new BadRequestException(vaccine.getName() + " must be separated from " + previousVaccineCombo.getVaccine().getName() + " by at least " + vaccineTiming.getIntervalDays() + " days");
                                }
                            }
                        }
                    } else {
                        check = 1;
                        boolean isCurrentActivated = previousVaccineCombo.getVaccine().isActivated();
                        int requiredInterval = isCurrentActivated ? getBusinessIntervalAfterActiveVaccine() : getBusinessIntervalAfterInactiveVaccine();
                        long actualInterval = vaccineComboResponseDTO.getIntervalDays();

                        if (actualInterval < requiredInterval) {
                            throw new BadRequestException(vaccine.getName() + " must be separated from " + previousVaccineCombo.getVaccine().getName() + " by at least " + requiredInterval + " days");
                        }
                    }
                }

                VaccineCombo vaccineCombo = VaccineCombo.builder()
                        .id(vaccineComboId)
                        .combo(combo)
                        .vaccine(vaccine)
                        .intervalDays(vaccineComboResponseDTO.getIntervalDays())
                        .build();
                updatedVaccineCombo.add(vaccineCombo);
                previousVaccineCombo = vaccineComboResponseDTO;
            }
            combo.setVaccineCombos(updatedVaccineCombo);
        }

        return toComboResponseDTO(comboRepository.save(combo));
    }

    @Override
    public ComboResponseDTO undeleteCombo(Integer comboID) {
        Combo combo = comboRepository.findById(comboID)
                .orElseThrow(() -> new ElementNotFoundException("Combo not found with id: " + comboID));

        if (!combo.isDeleted()) {
            throw new UnchangedStateException("Combo is not deleted");
        }
        combo.setDeleted(false);
        return toComboResponseDTO(comboRepository.save(combo));
    }

    @Override
    public ComboResponseDTO deleteCombo(Integer comboID) {
        Combo combo = comboRepository.findById(comboID)
                .orElseThrow(() -> new ElementNotFoundException("Combo not found with id: " + comboID));

        combo.setDeleted(true);
        return toComboResponseDTO(comboRepository.save(combo));
    }
}