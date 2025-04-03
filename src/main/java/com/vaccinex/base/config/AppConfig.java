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

    public static String getJWTIssuer() { return getResourceBundle().getString("jwt.issuer"); }

    public static Integer getJWTTimeToLive() { return Integer.valueOf(getResourceBundle().getString("jwt.time-to-live")); }

    public static String getAllowedOrigin() {
        return getResourceBundle().getString("allowed.origins");
    }

}
