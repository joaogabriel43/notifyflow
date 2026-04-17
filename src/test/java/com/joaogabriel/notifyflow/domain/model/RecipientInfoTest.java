package com.joaogabriel.notifyflow.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RecipientInfo Value Object Tests")
class RecipientInfoTest {

    @Test
    @DisplayName("Valid construction with only email")
    void constructor_validWithOnlyEmail() {
        var recipientInfo = new RecipientInfo("user@example.com", null, null);

        assertEquals("user@example.com", recipientInfo.getEmail());
        assertNull(recipientInfo.getPhoneNumber());
        assertNull(recipientInfo.getDeviceToken());
    }

    @Test
    @DisplayName("Valid construction with only phone number")
    void constructor_validWithOnlyPhone() {
        var recipientInfo = new RecipientInfo(null, "+5511999999999", null);

        assertNull(recipientInfo.getEmail());
        assertEquals("+5511999999999", recipientInfo.getPhoneNumber());
        assertNull(recipientInfo.getDeviceToken());
    }

    @Test
    @DisplayName("Valid construction with only device token")
    void constructor_validWithOnlyDeviceToken() {
        var recipientInfo = new RecipientInfo(null, null, "fcm-device-token-abc123");

        assertNull(recipientInfo.getEmail());
        assertNull(recipientInfo.getPhoneNumber());
        assertEquals("fcm-device-token-abc123", recipientInfo.getDeviceToken());
    }

    @Test
    @DisplayName("Valid construction with all fields")
    void constructor_validWithAllFields() {
        var recipientInfo = new RecipientInfo("user@example.com", "+5511999999999", "device-token");

        assertEquals("user@example.com", recipientInfo.getEmail());
        assertEquals("+5511999999999", recipientInfo.getPhoneNumber());
        assertEquals("device-token", recipientInfo.getDeviceToken());
    }

    @Test
    @DisplayName("Throws exception when all fields are null")
    void constructor_throwsException_whenAllFieldsNull() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> new RecipientInfo(null, null, null));

        assertTrue(exception.getMessage().contains("at least one contact method"));
    }
}
