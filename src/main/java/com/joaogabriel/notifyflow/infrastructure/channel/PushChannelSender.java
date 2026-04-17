package com.joaogabriel.notifyflow.infrastructure.channel;

import com.joaogabriel.notifyflow.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stub implementation for Push notification channel sender.
 * Firebase Cloud Messaging integration will be added in Sprint 4.
 */
@Component("pushChannelSender")
public class PushChannelSender implements ChannelSender {

    private static final Logger log = LoggerFactory.getLogger(PushChannelSender.class);

    @Override
    public boolean send(Notification notification) {
        log.info("[STUB] Sending PUSH notification to device: {} | Body: {}",
                notification.getRecipientInfo().getDeviceToken(),
                notification.getTemplateContent().getBody());
        return true;
    }
}
