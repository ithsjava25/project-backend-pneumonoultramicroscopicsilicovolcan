package org.example.crimearchive.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = CaseNumberValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCasenumber {
    String message() default "K numret måste vara med typen K-YYYY-XXXXXX eller tomt";

    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default {};
}
