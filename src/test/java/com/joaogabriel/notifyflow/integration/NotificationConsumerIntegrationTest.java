package com.joaogabriel.notifyflow.integration;

import com.joaogabriel.notifyflow.BaseIntegrationTest;
import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;
import com.joaogabriel.notifyflow.domain.model.Notification;
import com.joaogabriel.notifyflow.domain.model.NotificationTemplate;
import com.joaogabriel.notifyflow.domain.model.RecipientInfo;
import com.joaogabriel.notifyflow.domain.port.out.NotificationRepositoryPort;
import com.joaogabriel.notifyflow.infrastructure.messaging.consumer.NotificationMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DisplayName("NotificationConsumer Integration Tests")
class NotificationConsumerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NotificationRepositoryPort notificationRepository;

    @BeforeEach
    void setUp() {}

    @Test
    @DisplayName("Should consume message and update notification status to DELIVERED")
    void consumeMessage_success() {
        UUID notificationId = UUID.randomUUID();
        
        Notification notification = new Notification(
                notificationId,
                "tenant-1",
                NotificationStatus.PENDING,
                Channel.EMAIL,
                List.of(),
                new RecipientInfo("test@test.com", null, null),
                new NotificationTemplate("Subject", "Body", java.util.Map.of()),
                List.of(),
                LocalDateTime.now(),
                null
        );
        
        notificationRepository.save(notification);

        NotificationMessage message = new NotificationMessage(
                notificationId,
                "tenant-1",
                Channel.EMAIL,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend("notifyflow.notifications", "notification.send", message);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            Notification updated = notificationRepository.findById(notificationId).orElseThrow();
            assertEquals(NotificationStatus.DELIVERED, updated.getStatus());
            assertTrue(updated.getAttempts().size() > 0);
        });
    }
}
