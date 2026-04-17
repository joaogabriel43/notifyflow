package com.joaogabriel.notifyflow.application.dto;

import com.joaogabriel.notifyflow.domain.enums.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * DTO for incoming notification send requests.
 */
public record SendNotificationRequest(
        @NotBlank(message = "Tenant ID is required")
        String tenantId,

        @NotNull(message = "Preferred channel is required")
        Channel preferredChannel,

        List<Channel> fallbackChannels,

        String recipientEmail,
        String recipientPhone,
        String recipientDeviceToken,

        String subject,
        @NotBlank(message = "Body is required")
        String body,

        Map<String, String> templateVariables
) {
}
