package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Notification;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Implementation of the NotificationDao interface
 */
@ApplicationScoped
public class NotificationDaoImpl extends AbstractDao<Notification, Integer> implements NotificationDao {

    public NotificationDaoImpl() {
        super(Notification.class);
    }

    @Override
    public List<Notification> findByScheduleCustomerIdOrderByDateDesc(Integer id) {
        TypedQuery<Notification> query = entityManager.createQuery(
                "SELECT n FROM Notification n WHERE n.schedule.customer.id = :id ORDER BY n.date DESC", 
                Notification.class);
        query.setParameter("id", id);
        return query.getResultList();
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            entityManager.persist(notification);
            return notification;
        } else {
            return entityManager.merge(notification);
        }
    }
}