package com.vaccinex.service;

import com.sba301.vaccinex.dto.response.VaccineInventoryAlert;

import java.util.List;

public interface VaccineInventoryNotificationService {
    List<VaccineInventoryAlert> getVaccineInventoryAlerts(Integer days);
}
