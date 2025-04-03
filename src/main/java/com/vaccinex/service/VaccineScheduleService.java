package com.vaccinex.service;

import com.vaccinex.dto.request.VaccineDraftRequest;
import com.vaccinex.dto.response.CustomerScheduleResponse;
import com.vaccinex.dto.response.DoctorScheduleResponse;
import com.vaccinex.dto.response.ScheduleDetail;
import com.vaccinex.dto.response.VaccineScheduleDTO;
import com.vaccinex.pojo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VaccineScheduleService {

    /**
     * Delete draft schedules for a child
     * @param childId ID of the child
     */
    void deleteDraftSchedules(Integer childId);

    /**
     * Update a draft schedule with a new date
     * @param vaccineScheduleId ID of the schedule to update
     * @param newDate New date for the schedule
     */
    void updateSchedule(Integer vaccineScheduleId, LocalDateTime newDate);

    /**
     * Update an existing schedule with a new date
     * @param vaccineScheduleId ID of the schedule to update
     * @param newDate New date for the schedule
     */
    void updateExistingSchedule(Integer vaccineScheduleId, LocalDateTime newDate);

    /**
     * Get doctor's schedule for a specific date
     * @param doctorId ID of the doctor
     * @param date Date for the schedule (if null, uses current date)
     * @return List of doctor's schedules
     */
    List<DoctorScheduleResponse> getDoctorSchedule(Integer doctorId, LocalDate date);

    /**
     * Get doctor's history of completed or cancelled schedules
     * @param doctorId ID of the doctor
     * @return List of doctor's historical schedules
     */
    List<DoctorScheduleResponse> getDoctorHistory(Integer doctorId);

    /**
     * Get detailed information about a specific schedule
     * @param detailId ID of the schedule
     * @return Detailed schedule information
     */
    ScheduleDetail getScheduleDetails(Integer detailId);

    /**
     * Confirm a vaccination has been given
     * @param scheduleId ID of the schedule
     * @param doctorId ID of the doctor performing the vaccination
     * @return Result object
     */
    Object confirmVaccination(Integer scheduleId, Integer doctorId);

    /**
     * Handle callback for payment processing
     * @param orderId ID of the order
     */
    void handleCallback(Integer orderId);

    /**
     * Get all draft schedules for a child
     * @param childId ID of the child
     * @return List of draft schedules
     */
    List<VaccineScheduleDTO> getDrafts(Integer childId);

    /**
     * Create draft vaccination schedules
     * @param request Draft request information
     * @return List of created draft schedules
     */
    List<VaccineScheduleDTO> draftSchedule(VaccineDraftRequest request);

    /**
     * Create draft schedules for combo vaccines
     * @param doctor Doctor performing the vaccinations
     * @param child Child receiving the vaccinations
     * @param customer Customer (parent) of the child
     * @param firstDate Date of the first vaccination
     * @param combos List of vaccine combos
     * @return List of created schedules
     */
    List<VaccineSchedule> draftComboSchedules(User doctor, Child child, User customer, LocalDateTime firstDate, List<Combo> combos);

    /**
     * Create draft schedules for individual vaccines
     * @param doctor Doctor performing the vaccinations
     * @param child Child receiving the vaccinations
     * @param customer Customer (parent) of the child
     * @param firstDate Date of the first vaccination
     * @param vaccines List of vaccines
     * @return List of created schedules
     */
    List<VaccineSchedule> draftVaccineSchedules(User doctor, Child child, User customer, LocalDateTime firstDate, List<Vaccine> vaccines);

    /**
     * Get all vaccine schedules for a customer
     * @param customerId ID of the customer
     * @return List of customer's schedules
     */
    List<CustomerScheduleResponse> getVaccinesByCustomer(Integer customerId);
}