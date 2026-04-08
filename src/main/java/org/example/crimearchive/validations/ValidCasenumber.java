package org.example.crimearchive.validations;

import jakarta.validation.Constraint;

@Constraint(validatedBy = CaseNumberValidator.class)
public @interface ValidCasenumber {
    String message() default "K numret måste vara med typen K-YYYY-XXXXXX eller tomt";
}
