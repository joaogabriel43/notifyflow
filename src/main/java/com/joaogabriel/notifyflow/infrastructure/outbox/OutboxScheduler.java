package com.joaogabriel.notifyflow.infrastructure.outbox;

import com.joaogabriel.notifyflow.domain.port.out.NotificationOutboxPort;
import com.joaogabriel.notifyflow.infrastructure.messaging.publisher.NotificationPublisher;
import com.joaogabriel.notifyflow.infrastructure.persistence.entity.NotificationOutboxEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Outbox pattern scheduler that periodically polls the notification_outbox table
 * for pending entries and publishes them to RabbitMQ.
 *
 * Each entry is processed in its own @Transactional to avoid blocking the scheduler
 * if a single entry fails.
 */
@Component
@EnableScheduling
@ConditionalOnProperty(name = "scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxScheduler {

    private static final Logger log = LoggerFactory.getLogger(OutboxScheduler.class);

    private final NotificationOutboxPort outboxPort;
    private final NotificationPublisher publisher;

    public OutboxScheduler(NotificationOutboxPort outboxPort,
                           NotificationPublisher publisher) {
        this.outboxPort = outboxPort;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelay = 5000)
    public void processOutbox() {
        var pendingEvents = outboxPort.findPendingEvents(10);
        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Processing {} pending outbox entries", pendingEvents.size());

        for (NotificationOutboxEntity event : pendingEvents) {
            processEvent(event);
        }
    }

    @Transactional
    public void processEvent(NotificationOutboxEntity event) {
        try {
            publisher.publish(event.getPayload());
            outboxPort.markAsPublished(event.getId());
            log.info("Published outbox entry: {}", event.getId());
        } catch (Exception e) {
            log.error("Failed to publish outbox entry: {}. Error: {}", event.getId(), e.getMessage(), e);
            // Do NOT rethrow — prevents blocking the scheduler loop
        }
    }
}
