package com.vaccinex.dao;

import com.vaccinex.base.dao.GenericDao;
import com.vaccinex.pojo.Notification;
import java.util.List;

/**
 * Notification Data Access Object interface
 */
public interface NotificationDao extends GenericDao<Notification, Integer> {
    List<Notification> findByScheduleCustomerIdOrderByDateDesc(Integer id);
}
