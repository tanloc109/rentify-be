package com.vaccinex.base.config;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.TimeZone;

@Singleton
@Startup
public class TimezoneConfig {
    
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+7"));
        System.out.println("Application timezone set to: " + TimeZone.getDefault().getID());
    }
}