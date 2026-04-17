package com.joaogabriel.notifyflow.domain.model;

/**
 * Value Object representing the recipient information for a notification.
 * At least one of email, phoneNumber, or deviceToken must be non-null.
 */
public class RecipientInfo {

    private final String email;
    private final String phoneNumber;
    private final String deviceToken;

    public RecipientInfo(String email, String phoneNumber, String deviceToken) {
        if (email == null && phoneNumber == null && deviceToken == null) {
            throw new IllegalArgumentException(
                    "RecipientInfo must have at least one contact method (email, phoneNumber, or deviceToken)");
        }
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceToken = deviceToken;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDeviceToken() {
        return deviceToken;
    }
}
