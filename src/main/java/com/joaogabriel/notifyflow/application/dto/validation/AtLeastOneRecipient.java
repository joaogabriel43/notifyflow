package com.joaogabriel.notifyflow.application.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation ensuring at least one recipient field
 * (recipientEmail, recipientPhone, or recipientDeviceToken) is provided.
 */
@Documented
@Constraint(validatedBy = AtLeastOneRecipientValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneRecipient {

    String message() default "At least one recipient (email, phone, or deviceToken) must be provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
