package com.joaogabriel.notifyflow.infrastructure.persistence.adapter;

import com.joaogabriel.notifyflow.domain.port.out.NotificationOutboxPort;
import com.joaogabriel.notifyflow.infrastructure.persistence.entity.NotificationOutboxEntity;
import com.joaogabriel.notifyflow.infrastructure.persistence.repository.NotificationOutboxJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Infrastructure adapter implementing the NotificationOutboxPort.
 * Persists outbox entries for reliable message publishing.
 */
@Component
public class NotificationOutboxAdapter implements NotificationOutboxPort {

    private final NotificationOutboxJpaRepository outboxRepository;

    public NotificationOutboxAdapter(NotificationOutboxJpaRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    @Override
    public void saveOutboxEntry(UUID notificationId, String payload) {
        var outbox = NotificationOutboxEntity.builder()
                .notificationId(notificationId)
                .payload("{\"notificationId\":\"" + notificationId + "\"}")
                .status("PENDING")
                .build();
        outboxRepository.save(outbox);
    }
}
