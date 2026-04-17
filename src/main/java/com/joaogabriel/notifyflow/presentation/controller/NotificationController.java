package com.joaogabriel.notifyflow.presentation.controller;

import com.joaogabriel.notifyflow.application.dto.DeliveryAttemptResponse;
import com.joaogabriel.notifyflow.application.dto.NotificationResponse;
import com.joaogabriel.notifyflow.application.dto.SendNotificationRequest;
import com.joaogabriel.notifyflow.application.usecase.GetNotificationUseCase;
import com.joaogabriel.notifyflow.application.usecase.SendNotificationUseCase;
import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for notification operations.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    private final SendNotificationUseCase sendNotificationUseCase;
    private final GetNotificationUseCase getNotificationUseCase;
    private final com.joaogabriel.notifyflow.application.service.RetryNotificationService retryNotificationService;

    public NotificationController(SendNotificationUseCase sendNotificationUseCase,
                                  GetNotificationUseCase getNotificationUseCase,
                                  com.joaogabriel.notifyflow.application.service.RetryNotificationService retryNotificationService) {
        this.sendNotificationUseCase = sendNotificationUseCase;
        this.getNotificationUseCase = getNotificationUseCase;
        this.retryNotificationService = retryNotificationService;
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "Retry a failed notification", description = "Resets status and requeues for delivery")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Notification retry accepted"),
            @ApiResponse(responseCode = "422", description = "Notification cannot be retried (not FAILED or EXHAUSTED)")
    })
    public ResponseEntity<NotificationResponse> retry(@PathVariable UUID id) {
        var response = retryNotificationService.retryNotification(id);
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping
    @Operation(summary = "Send a notification", description = "Creates a new notification and queues it for async delivery")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notification created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<NotificationResponse> send(@Valid @RequestBody SendNotificationRequest request) {
        var response = sendNotificationUseCase.execute(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieves a notification by its unique identifier")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification found"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(getNotificationUseCase.execute(id));
    }

    @GetMapping
    @Operation(summary = "List notifications", description = "Lists notifications filtered by tenant ID and optional status with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    public ResponseEntity<Page<NotificationResponse>> list(
            @RequestParam(required = false) String tenantId,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pageable = PageRequest.of(page, size);
        if (tenantId != null && !tenantId.isBlank()) {
            return ResponseEntity.ok(getNotificationUseCase.findByTenantId(tenantId.trim(), status, pageable));
        }
        return ResponseEntity.ok(getNotificationUseCase.findAll(status, pageable));
    }

    @GetMapping("/{id}/attempts")
    @Operation(summary = "List delivery attempts", description = "Lists all delivery attempts for a specific notification")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Attempts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<List<DeliveryAttemptResponse>> getAttempts(@PathVariable UUID id) {
        return ResponseEntity.ok(getNotificationUseCase.findAttemptsByNotificationId(id));
    }
}
