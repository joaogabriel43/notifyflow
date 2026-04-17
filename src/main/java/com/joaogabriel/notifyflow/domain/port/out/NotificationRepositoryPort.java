package com.joaogabriel.notifyflow.domain.port.out;

import com.joaogabriel.notifyflow.domain.model.Notification;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port for notification persistence operations.
 * Implemented by infrastructure adapters.
 */
public interface NotificationRepositoryPort {

    Notification save(Notification notification);

    Optional<Notification> findById(UUID id);
}
