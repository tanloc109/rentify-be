package com.vaccinex.base.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ResourceBundle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConfig {

    private static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("application");
    }

    public static String getJWTSecretKey() {
        return getResourceBundle().getString("jwt.secret");
    }

    public static String getJWTIssuer() {
        return getResourceBundle().getString("jwt.issuer");
    }

    public static Integer getJWTTimeToLive() {
        return Integer.valueOf(getResourceBundle().getString("jwt.time-to-live"));
    }

    public static String getAllowedOrigin() {
        return getResourceBundle().getString("allowed.origins");
    }

    public static String getProperty(String key) {
        try {
            return getResourceBundle().getString(key);
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer getIntProperty(String key) {
        try {
            return Integer.valueOf(getResourceBundle().getString(key));
        } catch (Exception e) {
            return null;
        }
    }

    public static Double getDoubleProperty(String key) {
        try {
            return Double.valueOf(getResourceBundle().getString(key));
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean getBooleanProperty(String key) {
        try {
            return Boolean.valueOf(getResourceBundle().getString(key));
        } catch (Exception e) {
            return null;
        }
    }

    // Price constants
    public static String getPriceVaccineDefault() {
        return getProperty("price.vaccine.default");
    }

    public static String getPriceVaccineBelow() {
        return getProperty("price.vaccine.below");
    }

    public static String getPriceVaccineHigher() {
        return getProperty("price.vaccine.higher");
    }

    public static String getPriceVaccineAvgBegin() {
        return getProperty("price.vaccine.avg-begin");
    }

    public static String getPriceVaccineAvgEnd() {
        return getProperty("price.vaccine.avg-end");
    }

    public static String getPriceComboDefault() {
        return getProperty("price.combo.default");
    }

    public static String getPriceComboBelow() {
        return getProperty("price.combo.below");
    }

    public static String getPriceComboHigher() {
        return getProperty("price.combo.higher");
    }

    public static String getPriceComboAvgBegin() {
        return getProperty("price.combo.avg-begin");
    }

    public static String getPriceComboAvgEnd() {
        return getProperty("price.combo.avg-end");
    }

    // Business configuration constants
    public static int getBusinessIntervalAfterActiveVaccine() {
        return getIntProperty("business.interval-after-active-vaccine");
    }

    public static int getBusinessIntervalAfterInactiveVaccine() {
        return getIntProperty("business.interval-after-inactive-vaccine");
    }
}