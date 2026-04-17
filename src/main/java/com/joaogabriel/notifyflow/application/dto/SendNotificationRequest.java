package com.joaogabriel.notifyflow.application.dto;

import com.joaogabriel.notifyflow.application.dto.validation.AtLeastOneRecipient;
import com.joaogabriel.notifyflow.domain.enums.Channel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

/**
 * DTO for incoming notification send requests.
 * Custom validation ensures at least one recipient field is provided.
 */
@AtLeastOneRecipient
public record SendNotificationRequest(
        @NotBlank(message = "Tenant ID is required")
        String tenantId,

        @NotNull(message = "Preferred channel is required")
        Channel preferredChannel,

        List<Channel> fallbackChannels,

        @Email(message = "Invalid email format")
        String recipientEmail,

        String recipientPhone,

        String recipientDeviceToken,

        @NotBlank(message = "Template subject is required")
        String templateSubject,

        @NotBlank(message = "Template body is required")
        String templateBody,

        Map<String, String> templateVariables
) {
}
