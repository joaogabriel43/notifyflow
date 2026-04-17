package com.joaogabriel.notifyflow.infrastructure.channel;

import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.enums.NotificationStatus;
import com.joaogabriel.notifyflow.domain.exception.ChannelDeliveryException;
import com.joaogabriel.notifyflow.domain.model.Notification;
import com.joaogabriel.notifyflow.domain.model.NotificationTemplate;
import com.joaogabriel.notifyflow.domain.model.RecipientInfo;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SendGridEmailSender Unit Tests")
class SendGridEmailSenderTest {

    @Mock
    private SendGrid sendGrid;

    private SendGridEmailSender emailSender;

    private Notification notification;

    @BeforeEach
    void setUp() {
        emailSender = new SendGridEmailSender(sendGrid, "noreply@test.com");
        notification = new Notification(
                UUID.randomUUID(),
                "tenant-1",
                NotificationStatus.PENDING,
                Channel.EMAIL,
                List.of(),
                new RecipientInfo("user@test.com", null, null),
                new NotificationTemplate("Subject", "Body", Map.of()),
                List.of(),
                LocalDateTime.now(),
                null
        );
    }

    @Test
    @DisplayName("Should send email successfully when return status 202")
    void send_success() throws IOException {
        Response response = new Response();
        response.setStatusCode(202);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        assertDoesNotThrow(() -> emailSender.send(notification));
        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    @DisplayName("Should throw ChannelDeliveryException when return status is not 202")
    void send_errorStatus() throws IOException {
        Response response = new Response();
        response.setStatusCode(400);
        response.setBody("Bad Request");
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        ChannelDeliveryException ex = assertThrows(ChannelDeliveryException.class, () -> emailSender.send(notification));
        assertTrue(ex.getMessage().contains("status code 400"));
    }

    @Test
    @DisplayName("Should throw ChannelDeliveryException when SendGrid throws IOException")
    void send_ioException() throws IOException {
        when(sendGrid.api(any(Request.class))).thenThrow(new IOException("Network error"));

        ChannelDeliveryException ex = assertThrows(ChannelDeliveryException.class, () -> emailSender.send(notification));
        assertTrue(ex.getMessage().contains("IO Error"));
    }
}
