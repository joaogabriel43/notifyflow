package com.joaogabriel.notifyflow.domain.port.out;

import java.util.UUID;

/**
 * Output port for the notification outbox pattern.
 * Handles persistence of outbox entries for reliable message publishing.
 */
public interface NotificationOutboxPort {

    void saveOutboxEntry(UUID notificationId, String payload);
}
