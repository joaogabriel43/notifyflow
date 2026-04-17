package com.joaogabriel.notifyflow.domain.model;

import com.joaogabriel.notifyflow.domain.enums.AttemptResult;
import com.joaogabriel.notifyflow.domain.enums.Channel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain entity representing a single delivery attempt for a notification.
 * Records the channel used, the result, and any error message.
 */
public class DeliveryAttempt {

    private final UUID id;
    private final Channel channel;
    private final AttemptResult result;
    private final String errorMessage;
    private final LocalDateTime attemptedAt;

    public DeliveryAttempt(UUID id, Channel channel, AttemptResult result, String errorMessage, LocalDateTime attemptedAt) {
        this.id = id;
        this.channel = channel;
        this.result = result;
        this.errorMessage = errorMessage;
        this.attemptedAt = attemptedAt;
    }

    public UUID getId() {
        return id;
    }

    public Channel getChannel() {
        return channel;
    }

    public AttemptResult getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }
}
