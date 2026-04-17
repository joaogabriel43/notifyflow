package com.joaogabriel.notifyflow.infrastructure.channel;

import com.joaogabriel.notifyflow.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Stub implementation for SMS channel sender.
 * Twilio integration will be added in Sprint 4.
 */
@Component("smsChannelSender")
public class SmsChannelSender implements ChannelSender {

    private static final Logger log = LoggerFactory.getLogger(SmsChannelSender.class);

    @Override
    public boolean send(Notification notification) {
        log.info("[STUB] Sending SMS notification to: {} | Body: {}",
                notification.getRecipientInfo().getPhoneNumber(),
                notification.getTemplateContent().getBody());
        return true;
    }
}
