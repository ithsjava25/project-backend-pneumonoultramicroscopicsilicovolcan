package org.example.crimearchive.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CaseNumberValidator implements ConstraintValidator<ValidCasenumber, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null || s.isBlank() || s.matches("^\\d{4}-\\d{6}$") || s.matches("^[Kk]-\\d{4}-\\d{6}$")) {
            return true;
        }
        return false;
    }
}
