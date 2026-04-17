package com.joaogabriel.notifyflow.application.dto.validation;

import com.joaogabriel.notifyflow.application.dto.SendNotificationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for @AtLeastOneRecipient — checks that at least one
 * of recipientEmail, recipientPhone, or recipientDeviceToken is non-blank.
 */
public class AtLeastOneRecipientValidator implements ConstraintValidator<AtLeastOneRecipient, SendNotificationRequest> {

    @Override
    public boolean isValid(SendNotificationRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }
        return isNotBlank(request.recipientEmail())
                || isNotBlank(request.recipientPhone())
                || isNotBlank(request.recipientDeviceToken());
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
