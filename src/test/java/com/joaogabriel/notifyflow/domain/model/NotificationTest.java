package com.joaogabriel.notifyflow.domain.model;

import com.joaogabriel.notifyflow.domain.enums.AttemptResult;
import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Notification Domain Tests")
class NotificationTest {

    private Notification notification;

    @BeforeEach
    void setUp() {
        var recipientInfo = new RecipientInfo("user@example.com", "+5511999999999", null);
        var template = new NotificationTemplate("Test Subject", "Test Body", Map.of("key", "value"));

        notification = new Notification(
                UUID.randomUUID(),
                "tenant-001",
                NotificationStatus.PENDING,
                Channel.EMAIL,
                new ArrayList<>(List.of(Channel.SMS, Channel.PUSH)),
                recipientInfo,
                template,
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("getNextChannel returns EMAIL when it is the preferred and no attempts exist")
    void getNextChannel_returnsPreferredChannel_whenNoAttempts() {
        var nextChannel = notification.getNextChannel();

        assertTrue(nextChannel.isPresent());
        assertEquals(Channel.EMAIL, nextChannel.get());
    }

    @Test
    @DisplayName("getNextChannel returns SMS when EMAIL has already been attempted and failed")
    void getNextChannel_returnsSms_whenEmailFailed() {
        var failedAttempt = new DeliveryAttempt(
                UUID.randomUUID(),
                Channel.EMAIL,
                AttemptResult.FAILED,
                "SMTP connection refused",
                LocalDateTime.now()
        );
        notification.registerAttempt(failedAttempt);

        var nextChannel = notification.getNextChannel();

        assertTrue(nextChannel.isPresent());
        assertEquals(Channel.SMS, nextChannel.get());
    }

    @Test
    @DisplayName("hasMoreChannels returns false when all channels have been exhausted")
    void hasMoreChannels_returnsFalse_whenAllChannelsExhausted() {
        notification.registerAttempt(new DeliveryAttempt(
                UUID.randomUUID(), Channel.EMAIL, AttemptResult.FAILED,
                "SMTP error", LocalDateTime.now()));
        notification.registerAttempt(new DeliveryAttempt(
                UUID.randomUUID(), Channel.SMS, AttemptResult.FAILED,
                "Twilio error", LocalDateTime.now()));
        notification.registerAttempt(new DeliveryAttempt(
                UUID.randomUUID(), Channel.PUSH, AttemptResult.FAILED,
                "FCM error", LocalDateTime.now()));

        assertFalse(notification.hasMoreChannels());
    }

    @Test
    @DisplayName("registerAttempt adds attempt correctly to the list")
    void registerAttempt_addsAttemptToList() {
        assertEquals(0, notification.getAttempts().size());

        var attempt = new DeliveryAttempt(
                UUID.randomUUID(),
                Channel.EMAIL,
                AttemptResult.SUCCESS,
                null,
                LocalDateTime.now()
        );
        notification.registerAttempt(attempt);

        assertEquals(1, notification.getAttempts().size());
        assertEquals(Channel.EMAIL, notification.getAttempts().get(0).getChannel());
        assertEquals(AttemptResult.SUCCESS, notification.getAttempts().get(0).getResult());
    }

    @Test
    @DisplayName("exhaustAllChannels changes status to EXHAUSTED")
    void exhaustAllChannels_changesStatusToExhausted() {
        assertEquals(NotificationStatus.PENDING, notification.getStatus());

        notification.exhaustAllChannels();

        assertEquals(NotificationStatus.EXHAUSTED, notification.getStatus());
    }

    @Test
    @DisplayName("markDelivered changes status to DELIVERED")
    void markDelivered_changesStatusToDelivered() {
        assertEquals(NotificationStatus.PENDING, notification.getStatus());

        notification.markDelivered();

        assertEquals(NotificationStatus.DELIVERED, notification.getStatus());
    }

    @Test
    @DisplayName("markFailed changes status to FAILED")
    void markFailed_changesStatusToFailed() {
        assertEquals(NotificationStatus.PENDING, notification.getStatus());

        notification.markFailed();

        assertEquals(NotificationStatus.FAILED, notification.getStatus());
    }

    @Test
    @DisplayName("getNextChannel skips to PUSH when both EMAIL and SMS failed")
    void getNextChannel_returnsPush_whenEmailAndSmsFailed() {
        notification.registerAttempt(new DeliveryAttempt(
                UUID.randomUUID(), Channel.EMAIL, AttemptResult.FAILED,
                "Error", LocalDateTime.now()));
        notification.registerAttempt(new DeliveryAttempt(
                UUID.randomUUID(), Channel.SMS, AttemptResult.FAILED,
                "Error", LocalDateTime.now()));

        var nextChannel = notification.getNextChannel();

        assertTrue(nextChannel.isPresent());
        assertEquals(Channel.PUSH, nextChannel.get());
    }
}
