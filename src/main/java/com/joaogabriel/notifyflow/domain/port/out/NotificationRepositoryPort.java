package com.joaogabriel.notifyflow.domain.port.out;

import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;
import com.joaogabriel.notifyflow.domain.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Output port for notification persistence operations.
 * Implemented by infrastructure adapters.
 */
public interface NotificationRepositoryPort {

    Notification save(Notification notification);

    Optional<Notification> findById(UUID id);

    Page<Notification> findByTenantIdAndStatus(String tenantId, NotificationStatus status, Pageable pageable);
}
