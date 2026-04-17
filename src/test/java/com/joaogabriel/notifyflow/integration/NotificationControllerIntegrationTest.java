package com.joaogabriel.notifyflow.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaogabriel.notifyflow.application.dto.SendNotificationRequest;
import com.joaogabriel.notifyflow.domain.enums.Channel;
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
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("NotificationController Integration Tests")
class NotificationControllerIntegrationTest extends com.joaogabriel.notifyflow.BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationJpaRepository notificationRepository;

    @Autowired
    private NotificationOutboxJpaRepository outboxRepository;

    @BeforeEach
    void setUp() {
        outboxRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/notifications with valid payload returns 201 with id and status PENDING")
    void post_validPayload_returns201() throws Exception {
        var request = new SendNotificationRequest(
                "tenant-001",
                Channel.EMAIL,
                List.of(Channel.SMS, Channel.PUSH),
                "user@example.com",
                null, null,
                "Test Subject",
                "Test Body",
                Map.of("key", "value")
        );

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.tenantId").value("tenant-001"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.preferredChannel").value("EMAIL"))
                .andExpect(jsonPath("$.recipientEmail").value("user@example.com"))
                .andExpect(jsonPath("$.templateSubject").value("Test Subject"))
                .andExpect(jsonPath("$.templateBody").value("Test Body"));
    }

    @Test
    @DisplayName("POST /api/v1/notifications without tenantId returns 400 with ProblemDetail")
    void post_missingTenantId_returns400() throws Exception {
        var request = new SendNotificationRequest(
                null,
                Channel.EMAIL,
                List.of(),
                "user@example.com",
                null, null,
                "Subject",
                "Body",
                null
        );

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", hasItem(containsString("tenantId"))));
    }

    @Test
    @DisplayName("POST /api/v1/notifications without any recipient returns 400 with ProblemDetail")
    void post_noRecipient_returns400() throws Exception {
        var request = new SendNotificationRequest(
                "tenant-001",
                Channel.EMAIL,
                List.of(),
                null, null, null,
                "Subject",
                "Body",
                null
        );

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors", hasItem(containsString("recipient"))));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/{id} with existing ID returns 200")
    void get_existingId_returns200() throws Exception {
        // First create a notification
        var request = new SendNotificationRequest(
                "tenant-001",
                Channel.EMAIL,
                List.of(),
                "user@example.com",
                null, null,
                "Subject",
                "Body",
                null
        );

        var createResult = mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        var responseBody = objectMapper.readTree(createResult.getResponse().getContentAsString());
        var id = responseBody.get("id").asText();

        // Then retrieve it
        mockMvc.perform(get("/api/v1/notifications/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.tenantId").value("tenant-001"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/{id} with non-existing ID returns 404 with ProblemDetail")
    void get_nonExistingId_returns404() throws Exception {
        var randomId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/notifications/{id}", randomId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Notification Not Found"))
                .andExpect(jsonPath("$.detail").value(containsString(randomId.toString())));
    }

    @Test
    @DisplayName("GET /api/v1/notifications?tenantId=tenant-001 returns 200 with paginated list")
    void get_listByTenant_returns200() throws Exception {
        // Create two notifications for tenant-001
        var request = new SendNotificationRequest(
                "tenant-001",
                Channel.EMAIL,
                List.of(),
                "user@example.com",
                null, null,
                "Subject 1",
                "Body 1",
                null
        );

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        var request2 = new SendNotificationRequest(
                "tenant-001",
                Channel.SMS,
                List.of(),
                null,
                "+5511999999999",
                null,
                "Subject 2",
                "Body 2",
                null
        );

        mockMvc.perform(post("/api/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        // List all for tenant-001
        mockMvc.perform(get("/api/v1/notifications")
                        .param("tenantId", "tenant-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].tenantId").value("tenant-001"));
    }
}
