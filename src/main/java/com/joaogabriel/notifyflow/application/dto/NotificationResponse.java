package com.joaogabriel.notifyflow.application.dto;

import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for notification responses.
 */
public record NotificationResponse(
        UUID id,
        String tenantId,
        NotificationStatus status,
        Channel preferredChannel,
        List<Channel> fallbackChannels,
        String recipientEmail,
        String recipientPhone,
        String recipientDeviceToken,
        String templateSubject,
        String templateBody,
        List<DeliveryAttemptResponse> attempts,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
