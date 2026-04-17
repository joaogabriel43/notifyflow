package com.joaogabriel.notifyflow.domain.exception;

import java.util.UUID;

public class AllChannelsExhaustedException extends RuntimeException {

    public AllChannelsExhaustedException(UUID notificationId) {
        super("All channels exhausted for notification: " + notificationId);
    }

    public AllChannelsExhaustedException(String message) {
        super(message);
    }
}
