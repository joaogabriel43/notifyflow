package com.joaogabriel.notifyflow.infrastructure.channel;

import com.joaogabriel.notifyflow.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stub implementation for email channel sender.
 * SendGrid integration will be added in Sprint 4.
 */
@Component("emailChannelSender")
public class EmailChannelSender implements ChannelSender {

    private static final Logger log = LoggerFactory.getLogger(EmailChannelSender.class);

    @Override
    public boolean send(Notification notification) {
        log.info("[STUB] Sending EMAIL notification to: {} | Subject: {}",
                notification.getRecipientInfo().getEmail(),
                notification.getTemplateContent().getSubject());
        return true;
    }
}
