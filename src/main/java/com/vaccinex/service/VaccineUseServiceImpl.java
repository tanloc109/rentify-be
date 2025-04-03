package com.vaccinex.service;

import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dao.VaccineUseDao;
import com.vaccinex.dto.request.VaccineUseCreateRequest;
import com.vaccinex.dto.request.VaccineUseUpdateRequest;
import com.vaccinex.dto.response.VaccineUseResponseDTO;
import com.vaccinex.mapper.VaccineUseMapper;
import com.vaccinex.pojo.VaccineUse;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class VaccineUseServiceImpl extends BaseServiceImpl<VaccineUse, Integer> implements VaccineUseService {

    @Inject
    private VaccineUseDao vaccineUseRepository;

    @Inject
    private VaccineUseMapper vaccineUseMapper;

    public VaccineUseServiceImpl() {
        super(VaccineUse.class);
    }

    @Override
    public List<VaccineUseResponseDTO> getAllPurposes() {
        return vaccineUseRepository.findAll().stream()
                .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VaccineUseResponseDTO> getAllPurposesActive() {
        return vaccineUseRepository.findByDeletedIsFalse().stream()
                .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VaccineUseResponseDTO> getPurposes() {
        return vaccineUseRepository.findAll().stream()
                .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VaccineUseResponseDTO> getPurposesActive() {
        return vaccineUseRepository.findByDeletedIsFalse().stream()
                .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VaccineUseResponseDTO createPurpose(VaccineUseCreateRequest vaccineUseCreateRequest) {
        // Check if a purpose with the same name already exists
        VaccineUse existingPurpose = vaccineUseRepository.findByName(vaccineUseCreateRequest.getName());
        if (existingPurpose != null) {
            throw new ElementExistException("Purpose already exists with name: " + vaccineUseCreateRequest.getName());
        }

        // Create new VaccineUse
        VaccineUse vaccineUse = VaccineUse.builder()
                .name(vaccineUseCreateRequest.getName())
                .description(vaccineUseCreateRequest.getDescription())
                .build();

        // Save and convert to DTO
        vaccineUse = vaccineUseRepository.save(vaccineUse);
        return vaccineUseMapper.vaccineUseToVaccineUseResponseDTO(vaccineUse);
    }

    @Override
    @Transactional
    public VaccineUseResponseDTO updatePurpose(VaccineUseUpdateRequest vaccineUseUpdateRequest, int purposeID) {
        // Find the existing purpose
        VaccineUse vaccineUse = vaccineUseRepository.findById(purposeID)
                .orElseThrow(() -> new ElementNotFoundException("Purpose not found with ID: " + purposeID));

        // Check if new name already exists (if name is being changed)
        if (StringUtils.isNotBlank(vaccineUseUpdateRequest.getName())) {
            VaccineUse existingWithName = vaccineUseRepository.findByName(vaccineUseUpdateRequest.getName());
            if (existingWithName != null && !existingWithName.getId().equals(purposeID)) {
                throw new ElementExistException("Purpose already exists with name: " + vaccineUseUpdateRequest.getName());
            }
            vaccineUse.setName(vaccineUseUpdateRequest.getName());
        }

        // Update description if provided
        if (StringUtils.isNotBlank(vaccineUseUpdateRequest.getDescription())) {
            vaccineUse.setDescription(vaccineUseUpdateRequest.getDescription());
        }

        // Save and convert to DTO
        vaccineUse = vaccineUseRepository.save(vaccineUse);
        return vaccineUseMapper.vaccineUseToVaccineUseResponseDTO(vaccineUse);
    }

    @Override
    @Transactional
    public VaccineUseResponseDTO deletePurpose(Integer purposeID) {
        // Find the existing purpose
        VaccineUse vaccineUse = vaccineUseRepository.findById(purposeID)
                .orElseThrow(() -> new ElementNotFoundException("Purpose not found with ID: " + purposeID));

        // Soft delete
        vaccineUse.setDeleted(true);
        vaccineUse = vaccineUseRepository.save(vaccineUse);
        return vaccineUseMapper.vaccineUseToVaccineUseResponseDTO(vaccineUse);
    }

    @Override
    @Transactional
    public VaccineUseResponseDTO undeletePurpose(Integer purposeID) {
        // Find the existing purpose
        VaccineUse vaccineUse = vaccineUseRepository.findById(purposeID)
                .orElseThrow(() -> new ElementNotFoundException("Purpose not found with ID: " + purposeID));

        // Check if already not deleted
        if (!vaccineUse.isDeleted()) {
            throw new UnchangedStateException("Purpose is not deleted");
        }

        // Restore
        vaccineUse.setDeleted(false);
        vaccineUse = vaccineUseRepository.save(vaccineUse);
        return vaccineUseMapper.vaccineUseToVaccineUseResponseDTO(vaccineUse);
    }

    @Override
    public List<VaccineUseResponseDTO> searchVaccineUses(String name) {
        // If no name provided, return all active purposes
        if (StringUtils.isBlank(name)) {
            return getAllPurposesActive();
        }

        // Search by name (case-insensitive)
        return vaccineUseRepository.findByDeletedIsFalse().stream()
                .filter(vaccineUse -> StringUtils.containsIgnoreCase(vaccineUse.getName(), name))
                .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                .collect(Collectors.toList());
    }
}