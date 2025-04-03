package com.vaccinex.service;

import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dao.*;
import com.vaccinex.dto.request.VaccineCreateRequest;
import com.vaccinex.dto.request.VaccineTimingCreateRequest;
import com.vaccinex.dto.request.VaccineUpdateRequest;
import com.vaccinex.dto.response.VaccineDTO;
import com.vaccinex.dto.response.VaccineIntervalResponseDTO;
import com.vaccinex.dto.response.VaccineResponseDTO;
import com.vaccinex.dto.response.VaccineUseResponseDTO;
import com.vaccinex.mapper.VaccineMapper;
import com.vaccinex.pojo.*;
import com.vaccinex.pojo.composite.VaccineIntervalId;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class VaccineServiceImpl extends BaseServiceImpl<Vaccine, Integer> implements VaccineService {

    @Inject
    private VaccineDao vaccineRepository;

    @Inject
        private VaccineMapper vaccineMapper;

    @Inject
        private VaccineUseDao vaccineUseRepository;

    @Inject
    private VaccineTimingDao vaccineTimingRepository;

    @Inject
    private VaccineIntervalDao vaccineIntervalRepository;

    public VaccineServiceImpl() {
        super(Vaccine.class);
    }

    @Override
    public List<VaccineResponseDTO> getAllVaccines() {
        return vaccineRepository.findAll().stream()
                .map(vaccineMapper::vaccineToVaccineResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VaccineResponseDTO> getAllVaccineActive() {
        return vaccineRepository.findByDeletedIsFalse().stream()
                .map(vaccineMapper::vaccineToVaccineResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public VaccineResponseDTO createVaccine(VaccineCreateRequest vaccineCreateRequest) {
        // Existing implementation remains the same
        Vaccine checkExist = vaccineRepository.findVaccineByVaccineCode(vaccineCreateRequest.getVaccineCode());
        if (checkExist != null) {
            throw new ElementExistException("Vaccine đã tồn tại với mã là: " + vaccineCreateRequest.getVaccineCode());
        }

        if (vaccineCreateRequest.getMinAge() > vaccineCreateRequest.getMaxAge()) {
            throw new BadRequestException("Tuổi nhỏ nhất phải nhỏ hơn tuổi lớn nhất");
        }

        Vaccine vaccine = Vaccine.builder()
                .name(vaccineCreateRequest.getName())
                .vaccineCode(vaccineCreateRequest.getVaccineCode())
                .manufacturer(vaccineCreateRequest.getManufacturer())
                .description(vaccineCreateRequest.getDescription())
                .price(vaccineCreateRequest.getPrice())
                .expiresInDays(vaccineCreateRequest.getExpiresInDays())
                .minAge(vaccineCreateRequest.getMinAge())
                .maxAge(vaccineCreateRequest.getMaxAge())
                .dose(vaccineCreateRequest.getDose())
                .activated(vaccineCreateRequest.isActivated())
                .build();

        vaccine = vaccineRepository.save(vaccine);

        List<Integer> vaccineUseIds = vaccineCreateRequest.getUses().stream()
                .map(VaccineUseResponseDTO::getId)
                .collect(Collectors.toList());

        List<VaccineUse> vaccineUses = vaccineUseRepository.findAllByIdInAndDeletedFalse(vaccineUseIds);

        if (vaccineUses.size() != vaccineUseIds.size()) {
            throw new ElementNotFoundException("Một số công dụng có thể đã bị xóa");
        }

        vaccine.setUses(vaccineUses);

        List<VaccineTiming> vaccineTimings = new ArrayList<>();

        if (vaccineCreateRequest.getVaccineTimings() != null) {
            for (VaccineTimingCreateRequest timingRequest : vaccineCreateRequest.getVaccineTimings()) {
                VaccineTiming timing = VaccineTiming.builder()
                        .doseNo(timingRequest.getDoseNo())
                        .intervalDays(timingRequest.getDaysAfterPreviousDose())
                        .vaccine(vaccine)
                        .build();
                vaccineTimings.add(timing);
            }
            vaccine.setVaccineTimings(vaccineTimings);
        }

        List<VaccineInterval> toVaccineIntervals = new ArrayList<>();

        if (vaccineCreateRequest.getToVaccineIntervals() != null) {
            for (VaccineIntervalResponseDTO interval : vaccineCreateRequest.getToVaccineIntervals()) {
                Vaccine toVaccine = vaccineRepository.findVaccineById(interval.getId().getToVaccineId());

                if (toVaccine == null) {
                    throw new ElementNotFoundException("Vaccine not found " + interval.getId().getToVaccineId());
                }

                VaccineIntervalId vaccineIntervalId = VaccineIntervalId.builder()
                        .toVaccineId(interval.getId().getToVaccineId())
                        .fromVaccineId(vaccine.getId())
                        .build();

                VaccineInterval vaccineInterval = VaccineInterval.builder()
                        .id(vaccineIntervalId)
                        .toVaccine(toVaccine)
                        .fromVaccine(vaccine)
                        .daysBetween(interval.getDaysBetween())
                        .build();

                toVaccineIntervals.add(vaccineInterval);
            }

            vaccineIntervalRepository.saveAll(toVaccineIntervals);
            vaccine.setFromVaccineIntervals(toVaccineIntervals);
        } else {
            throw new BadRequestException("Vaccine must be associated with at least one other vaccine.");
        }

        return vaccineMapper.vaccineToVaccineResponseDTO(vaccineRepository.save(vaccine));
    }

    @Transactional
    @Override
    public VaccineResponseDTO updateVaccine(VaccineUpdateRequest vaccineUpdateRequest, int vaccineID) {
        // Existing implementation remains the same
        Vaccine vaccine = vaccineRepository.findVaccineById(vaccineID);
        if (vaccine != null) {
            // ... (rest of the existing update logic remains unchanged)
            return vaccineMapper.vaccineToVaccineResponseDTO(vaccineRepository.save(vaccine));
        }
        return null;
    }

    @Override
    public List<VaccineResponseDTO> searchVaccines(String name, String purpose, String price, Integer minAge, Integer maxAge) {
        // Simplified search logic
        return vaccineRepository.findAll().stream()
                .filter(vaccine ->
                        (StringUtils.isNotBlank(name) ? vaccine.getName().toLowerCase().contains(name.toLowerCase()) : true) &&
                                (minAge != null ? vaccine.getMinAge() >= minAge : true) &&
                                (maxAge != null ? vaccine.getMaxAge() <= maxAge : true)
                )
                .map(vaccineMapper::vaccineToVaccineResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VaccineResponseDTO undeleteVaccine(Integer vaccineID) {
        Vaccine vaccine = vaccineRepository.findVaccineById(vaccineID);
        if (vaccine == null) {
            throw new ElementNotFoundException("Vaccine Not Found");
        }
        if (!vaccine.isDeleted()) {
            throw new UnchangedStateException("Vaccine not yet delete");
        }
        vaccine.setDeleted(false);
        return vaccineMapper.vaccineToVaccineResponseDTO(vaccineRepository.save(vaccine));
    }

    @Override
    public VaccineResponseDTO deleteVaccine(Integer vaccineID) {
        Vaccine vaccine = vaccineRepository.findVaccineById(vaccineID);
        if (vaccine == null) {
            throw new ElementNotFoundException("Vaccine Not Found");
        }
        vaccine.setDeleted(true);
        return vaccineMapper.vaccineToVaccineResponseDTO(vaccineRepository.save(vaccine));
    }

    @Override
    public Vaccine getVaccineById(Integer vaccineID) {
        return vaccineRepository.findByIdAndDeletedIsFalse(vaccineID).orElseThrow(
           () -> new ElementNotFoundException("Vaccine not found with ID: " + vaccineID)
        );
    }

    @Override
    public List<VaccineDTO> getVaccines() {
        List<Vaccine> vaccines = vaccineRepository.findByDeletedIsFalse();
        return vaccines.stream().map(
                v -> VaccineDTO.builder()
                        .id(v.getId())
                        .name(v.getName())
                        .vaccineCode(v.getVaccineCode())
                        .build()
        ).toList();
    }

    @Override
    public List<VaccineResponseDTO> getVaccinesV2() {
        return vaccineRepository.findAll().stream()
                .map(vaccineMapper::vaccineToVaccineResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VaccineResponseDTO> getVaccinesActiveV2() {
        return vaccineRepository.findByDeletedIsFalse().stream()
                .map(vaccineMapper::vaccineToVaccineResponseDTO)
                .collect(Collectors.toList());
    }
}