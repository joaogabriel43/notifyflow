package com.joaogabriel.notifyflow.domain.port.out;

import com.joaogabriel.notifyflow.infrastructure.persistence.entity.NotificationOutboxEntity;

import java.util.List;
import java.util.UUID;

/**
 * Output port for the notification outbox pattern.
 * Handles persistence of outbox entries for reliable message publishing.
 */
public interface NotificationOutboxPort {

    void saveOutboxEntry(UUID notificationId, String payload);

    List<NotificationOutboxEntity> findPendingEvents(int limit);

    void markAsPublished(UUID id);
}
