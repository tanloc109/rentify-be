package com.vaccinex.service;

import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineTiming;


public interface VaccineTimingService {
    VaccineTiming getVaccineTimingById(Integer id);

    VaccineTiming getVaccineTimingByVaccine(Vaccine vaccine, int doseNo);
}
