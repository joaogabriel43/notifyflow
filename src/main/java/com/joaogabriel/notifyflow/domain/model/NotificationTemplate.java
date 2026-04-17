package com.joaogabriel.notifyflow.domain.model;

import java.util.Map;

/**
 * Value Object representing the template content for a notification.
 * Contains the subject, body and optional template variables for dynamic content.
 */
public class NotificationTemplate {

    private final String subject;
    private final String body;
    private final Map<String, String> templateVariables;

    public NotificationTemplate(String subject, String body, Map<String, String> templateVariables) {
        this.subject = subject;
        this.body = body;
        this.templateVariables = templateVariables != null ? Map.copyOf(templateVariables) : Map.of();
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getTemplateVariables() {
        return templateVariables;
    }
}
