package com.vaccinex.auth.contants;

public class AuthExceptionMessage {
    public static final String EMAIL_ALREADY_USED = "The email is already in use!";
    public static final String PHONE_NUMBER_INVALID = "The phone number is not valid!";
    public static final String PASSWORDS_DO_NOT_MATCH = "Password confirmation does not match!";
    public static final String INVALID_PASSWORD_FORMAT = "The password must be at least 8 characters long and include uppercase letters, lowercase letters, numbers, and special characters!";
    public static final String INVALID_ROLE = "Invalid role! Only 'RENTER' or 'HOST' are allowed.";
    public static final String INVALID_EMAIL_OR_PASSWORD = "Email or password is incorrect.";
    public static final String ACCOUNT_DELETED = "This Account is deleted.";
}