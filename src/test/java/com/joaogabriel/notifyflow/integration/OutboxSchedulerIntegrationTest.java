package com.joaogabriel.notifyflow.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaogabriel.notifyflow.application.dto.SendNotificationRequest;
import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.infrastructure.persistence.entity.OutboxStatus;
import com.joaogabriel.notifyflow.infrastructure.persistence.repository.NotificationJpaRepository;
import com.joaogabriel.notifyflow.infrastructure.persistence.repository.NotificationOutboxJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("OutboxScheduler Integration Tests")
class OutboxSchedulerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationJpaRepository notificationRepository;

    @Autowired
    private NotificationOutboxJpaRepository outboxRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        outboxRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("Creating a notification via POST also creates a PENDING outbox entry")
    void postNotification_createsOutboxEntry() throws Exception {
        var request = new SendNotificationRequest(
                "tenant-outbox-test",
                Channel.EMAIL,
                List.of(Channel.SMS),
                "outbox@example.com",
                null, null,
                "Outbox Test Subject",
                "Outbox Test Body",
                null
        );

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verify outbox entry was created
        var outboxEntries = outboxRepository.findTop10ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        assertFalse(outboxEntries.isEmpty(), "Outbox should have at least one PENDING entry");

        var entry = outboxEntries.get(0);
        assertEquals(OutboxStatus.PENDING, entry.getStatus());
        assertNotNull(entry.getPayload());
        assertNotNull(entry.getNotificationId());
        assertNull(entry.getPublishedAt());
    }

    @Test
    @DisplayName("Multiple notifications create corresponding outbox entries")
    void multipleNotifications_createMultipleOutboxEntries() throws Exception {
        for (int i = 0; i < 3; i++) {
            var request = new SendNotificationRequest(
                    "tenant-outbox-multi",
                    Channel.EMAIL,
                    List.of(),
                    "user" + i + "@example.com",
                    null, null,
                    "Subject " + i,
                    "Body " + i,
                    null
            );

            mockMvc.perform(post("/api/v1/notifications")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        var outboxEntries = outboxRepository.findTop10ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        assertEquals(3, outboxEntries.size(), "Should have 3 pending outbox entries");
    }
}
