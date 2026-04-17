package com.joaogabriel.notifyflow.infrastructure.messaging.consumer;

import com.joaogabriel.notifyflow.domain.enums.Channel;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationMessage(
        UUID notificationId,
        String tenantId,
        Channel channel,
        LocalDateTime createdAt
) {}
