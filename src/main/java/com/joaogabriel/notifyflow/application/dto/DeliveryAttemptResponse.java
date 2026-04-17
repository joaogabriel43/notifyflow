package com.joaogabriel.notifyflow.application.dto;

import com.joaogabriel.notifyflow.domain.enums.AttemptResult;
import com.joaogabriel.notifyflow.domain.enums.Channel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for delivery attempt responses.
 */
public record DeliveryAttemptResponse(
        UUID id,
        Channel channel,
        AttemptResult result,
        String errorMessage,
        LocalDateTime attemptedAt
) {
}
