package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.User;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineSchedule;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * VaccineSchedule Data Access Object interface
 */
public interface VaccineScheduleDao extends GenericDao<VaccineSchedule, Integer> {
    List<VaccineSchedule> findByDeletedIsFalseAndStatus(VaccineScheduleStatus status);
    List<VaccineSchedule> findByChildIdAndVaccineIdOrderByDateAsc(Integer childId, Integer vaccineId);
    void deleteByStatusAndChildId(VaccineScheduleStatus status, Integer childId);
    List<VaccineSchedule> findByStatusAndChildId(VaccineScheduleStatus status, Integer childId);
    List<VaccineSchedule> findByChildIdAndDateAfterOrderByDateAsc(Integer childId, LocalDateTime date);
    List<VaccineSchedule> findByCustomer(User customer);
    boolean existsByDoctorAndDate(User doctor, LocalDateTime date);
    List<VaccineSchedule> findByDeletedIsFalseAndStatusAndDateIsBetweenOrderByDateAsc(
            VaccineScheduleStatus status, LocalDateTime dateAfter, LocalDateTime dateBefore);
    int countBatch(int batchId);
    List<VaccineSchedule> findByChildIdOrderByDateDesc(Integer childId);
    List<VaccineSchedule> findByDoctorId(Integer doctorId);
    Integer countByVaccine(Vaccine vaccine);
}
