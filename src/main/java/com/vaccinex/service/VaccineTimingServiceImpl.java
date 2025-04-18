package com.vaccinex.service;

import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.dao.VaccineTimingDao;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineTiming;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

@Stateless
public class VaccineTimingServiceImpl implements VaccineTimingService {


    @Inject
    private VaccineTimingDao vaccineTimingRepository;

    @Override
    public VaccineTiming getVaccineTimingById(Integer id) {
        return vaccineTimingRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                () -> new ElementNotFoundException("Không tìm thấy thời điểm tiêm chủng với ID: " + id)
        );
    }

    @Override
    public VaccineTiming getVaccineTimingByVaccine(Vaccine vaccine, int doseNo) {
        return vaccineTimingRepository.findByVaccineAndDoseNoAndDeletedIsFalse(vaccine, doseNo).orElseThrow(
                () -> new ElementNotFoundException("Không tìm thấy thời điểm tiêm chủng với vaccine: " + vaccine)
        );
    }
}
