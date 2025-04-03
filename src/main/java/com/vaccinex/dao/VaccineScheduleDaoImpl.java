package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.User;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineSchedule;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the VaccineScheduleDao interface
 */
@ApplicationScoped
public class VaccineScheduleDaoImpl extends AbstractDao<VaccineSchedule, Integer> implements VaccineScheduleDao {

    public VaccineScheduleDaoImpl() {
        super(VaccineSchedule.class);
    }

    @Override
    public List<VaccineSchedule> findByDeletedIsFalseAndStatus(VaccineScheduleStatus status) {
        TypedQuery<VaccineSchedule> query = entityManager.createQuery(
                "SELECT vs FROM VaccineSchedule vs WHERE vs.deleted = false AND vs.status = :status", 
                VaccineSchedule.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<VaccineSchedule> findByChildIdAndVaccineIdOrderByDateAsc(Integer childId, Integer vaccineId) {
        TypedQuery<VaccineSchedule> query = entityManager.createQuery(
                "SELECT vs FROM VaccineSchedule vs WHERE vs.child.id = :childId AND vs.vaccine.id = :vaccineId ORDER BY vs.date ASC", 
                VaccineSchedule.class);
        query.setParameter("childId", childId);
        query.setParameter("vaccineId", vaccineId);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void deleteByStatusAndChildId(VaccineScheduleStatus status, Integer childId) {
        Query query = entityManager.createQuery(
                "DELETE FROM VaccineSchedule vs WHERE vs.status = :status AND vs.child.id = :childId");
        query.setParameter("status", status);
        query.setParameter("childId", childId);
        query.executeUpdate();
    }

    @Override
    public List<VaccineSchedule> findByStatusAndChildId(VaccineScheduleStatus status, Integer childId) {
        TypedQuery<VaccineSchedule> query = entityManager.createQuery(
                "SELECT vs FROM VaccineSchedule vs WHERE vs.status = :status AND vs.child.id = :childId", 
                VaccineSchedule.class);
        query.setParameter("status", status);
        query.setParameter("childId", childId);
        return query.getResultList();
    }

    @Override
    public List<VaccineSchedule> findByChildIdAndDateAfterOrderByDateAsc(Integer childId, LocalDateTime date) {
        TypedQuery<VaccineSchedule> query = entityManager.createQuery(
                "SELECT vs FROM VaccineSchedule vs WHERE vs.child.id = :childId AND vs.date > :date ORDER BY vs.date ASC", 
                VaccineSchedule.class);
        query.setParameter("childId", childId);
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public List<VaccineSchedule> findByCustomer(User customer) {
        TypedQuery<VaccineSchedule> query = entityManager.createQuery(
                "SELECT vs FROM VaccineSchedule vs WHERE vs.customer = :customer", 
                VaccineSchedule.class);
        query.setParameter("customer", customer);
        return query.getResultList();
    }

    @Override
    public boolean existsByDoctorAndDate(User doctor, LocalDateTime date) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(vs) FROM VaccineSchedule vs WHERE vs.doctor = :doctor AND vs.date = :date", 
                Long.class);
        query.setParameter("doctor", doctor);
        query.setParameter("date", date);
        return query.getSingleResult() > 0;
    }

    @Override
    public List<VaccineSchedule> findByDeletedIsFalseAndStatusAndDateIsBetweenOrderByDateAsc(
            VaccineScheduleStatus status, LocalDateTime dateAfter, LocalDateTime dateBefore) {
        TypedQuery<VaccineSchedule> query = entityManager.createQuery(
                "SELECT vs FROM VaccineSchedule vs WHERE vs.deleted = false AND vs.status = :status " +
                "AND vs.date >= :dateAfter AND vs.date <= :dateBefore ORDER BY vs.date ASC", 
                VaccineSchedule.class);
        query.setParameter("status", status);
        query.setParameter("dateAfter", dateAfter);
        query.setParameter("dateBefore", dateBefore);
        return query.getResultList();
    }

    @Override
    public int countBatch(int batchId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(vs) FROM VaccineSchedule vs WHERE vs.batch.id = :batchId AND vs.status = 'PLANNED'", 
                Long.class);
        query.setParameter("batchId", batchId);
        return query.getSingleResult().intValue();
    }

    @Override
    public List<VaccineSchedule> findByChildIdOrderByDateDesc(Integer childId) {
        TypedQuery<VaccineSchedule> query = entityManager.createQuery(
                "SELECT vs FROM VaccineSchedule vs WHERE vs.child.id = :childId ORDER BY vs.date DESC", 
                VaccineSchedule.class);
        query.setParameter("childId", childId);
        return query.getResultList();
    }

    @Override
    public List<VaccineSchedule> findByDoctorId(Integer doctorId) {
        TypedQuery<VaccineSchedule> query = entityManager.createQuery(
                "SELECT vs FROM VaccineSchedule vs WHERE vs.doctor.id = :doctorId", 
                VaccineSchedule.class);
        query.setParameter("doctorId", doctorId);
        return query.getResultList();
    }

    @Override
    public Integer countByVaccine(Vaccine vaccine) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(vs) FROM VaccineSchedule vs WHERE vs.vaccine = :vaccine", 
                Long.class);
        query.setParameter("vaccine", vaccine);
        return query.getSingleResult().intValue();
    }

    @Override
    @Transactional
    public VaccineSchedule save(VaccineSchedule vaccineSchedule) {
        if (vaccineSchedule.getId() == null) {
            entityManager.persist(vaccineSchedule);
            return vaccineSchedule;
        } else {
            return entityManager.merge(vaccineSchedule);
        }
    }
}