package com.joaogabriel.notifyflow.infrastructure.channel;

import com.joaogabriel.notifyflow.domain.enums.Channel;
import com.joaogabriel.notifyflow.domain.exception.ChannelDeliveryException;
import com.joaogabriel.notifyflow.domain.model.Notification;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("emailChannelSender")
@ConditionalOnProperty(name = "channels.email.provider", havingValue = "sendgrid")
public class SendGridEmailSender implements ChannelSender {
    private static final Logger log = LoggerFactory.getLogger(SendGridEmailSender.class);
    
    private final SendGrid sendGrid;
    private final String fromEmail;

    public SendGridEmailSender(SendGrid sendGrid, @Value("${channels.from-email}") String fromEmail) {
        this.sendGrid = sendGrid;
        this.fromEmail = fromEmail;
    }

    @Override
    public Channel getChannel() {
        return Channel.EMAIL;
    }

    @Override
    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "channelSender", fallbackMethod = "fallback")
    @io.github.resilience4j.retry.annotation.Retry(name = "channelSender")
    public void send(Notification notification) {
        log.info("Sending EMAIL notification to: {} | Subject: {}",
                notification.getRecipientInfo().getEmail(),
                notification.getTemplateContent().getSubject());
                
        Email from = new Email(fromEmail);
        String subject = notification.getTemplateContent().getSubject();
        Email to = new Email(notification.getRecipientInfo().getEmail());
        Content content = new Content("text/html", notification.getTemplateContent().getBody());
        Mail mail = new Mail(from, subject, to, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            
            if (response.getStatusCode() != 202) {
                throw new ChannelDeliveryException("SendGrid API returned status code " + response.getStatusCode() + ": " + response.getBody());
            }
        } catch (IOException ex) {
            throw new ChannelDeliveryException("IO Error sending email via SendGrid: " + ex.getMessage(), ex);
        }
    }

    public void fallback(Notification notification, Throwable t) {
        throw new ChannelDeliveryException("Fallback reached for EMAIL (SendGrid): " + t.getMessage(), t);
    }
}
