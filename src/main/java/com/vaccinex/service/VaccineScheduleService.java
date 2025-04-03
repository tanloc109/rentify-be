package com.vaccinex.service;


import com.vaccinex.dto.paging.PagingRequest;
import com.vaccinex.dto.paging.PagingResponse;
import com.vaccinex.dto.request.VaccineDraftRequest;
import com.vaccinex.dto.response.DoctorScheduleResponse;
import com.vaccinex.dto.response.ScheduleDetail;
import com.vaccinex.dto.response.VaccineScheduleDTO;
import com.vaccinex.pojo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VaccineScheduleService {

    void deleteDraftSchedules(Integer customerId);

    void updateSchedule(Integer vaccineScheduleId, LocalDateTime newDate);

    void updateExistingSchedule(Integer vaccineScheduleId, LocalDateTime newDate);

    List<DoctorScheduleResponse> getDoctorSchedule(Integer doctorId, LocalDate date);

    List<DoctorScheduleResponse> getDoctorHistory(Integer doctorId);

    ScheduleDetail getScheduleDetails(Integer detailId);

    Object confirmVaccination(Integer scheduleId, Integer doctorId);

    void handleCallback(Integer orderId);

    List<VaccineScheduleDTO> getDrafts(Integer childId);

    List<VaccineScheduleDTO> draftSchedule(VaccineDraftRequest request);

    List<VaccineSchedule> draftComboSchedules(User doctor, Child child, User customer, LocalDateTime firstDate, List<Combo> combos);

    List<VaccineSchedule> draftVaccineSchedules(User doctor, Child child, User customer, LocalDateTime firstDate, List<Vaccine> vaccines);

    PagingResponse getVaccinesByCustomer(Integer customerId, PagingRequest pagingRequest);
}
