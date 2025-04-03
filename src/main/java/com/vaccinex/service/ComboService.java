package com.vaccinex.service;

import com.vaccinex.dto.request.VaccineComboCreateRequest;
import com.vaccinex.dto.request.VaccineComboUpdateRequest;
import com.vaccinex.dto.response.ComboResponseDTO;
import com.vaccinex.pojo.Combo;

import java.util.List;

public interface ComboService extends BaseService<Combo, Integer> {

    /**
     * Convert a Combo entity to ComboResponseDTO
     * @param combo the combo to convert
     * @return the DTO representation
     */
    ComboResponseDTO toComboResponseDTO(Combo combo);

    /**
     * Get combo by ID, throws ElementNotFoundException if not found
     * @param id the combo ID
     * @return the found combo
     */
    Combo getComboById(Integer id);

    /**
     * Get all combos, active or inactive
     * @return list of all combo DTOs
     */
    List<ComboResponseDTO> getAllCombos();

    /**
     * Get all active combos
     * @return list of active combo DTOs
     */
    List<ComboResponseDTO> getAllCombosActive();

    /**
     * Search for combos matching specified criteria
     * @param name optional name filter
     * @param price optional price category filter ("low", "medium", "high")
     * @param minAge optional minimum age filter
     * @param maxAge optional maximum age filter
     * @return list of matching combo DTOs
     */
    List<ComboResponseDTO> searchVaccineCombos(String name, String price, Integer minAge, Integer maxAge);

    /**
     * Restore a deleted combo, throws ElementNotFoundException if not found
     * or UnchangedStateException if already active
     * @param comboID ID of combo to restore
     * @return the restored combo as DTO
     */
    ComboResponseDTO undeleteCombo(Integer comboID);

    /**
     * Create a new combo
     * @param request details for the new combo
     * @return the created combo as DTO
     */
    ComboResponseDTO createCombo(VaccineComboCreateRequest request);

    /**
     * Update an existing combo, throws ElementNotFoundException if not found
     * @param request update details
     * @param comboID ID of combo to update
     * @return the updated combo as DTO
     */
    ComboResponseDTO updateCombo(VaccineComboUpdateRequest request, int comboID);

    /**
     * Delete a combo (soft delete), throws ElementNotFoundException if not found
     * @param comboID ID of combo to delete
     * @return the deleted combo as DTO
     */
    ComboResponseDTO deleteCombo(Integer comboID);
}