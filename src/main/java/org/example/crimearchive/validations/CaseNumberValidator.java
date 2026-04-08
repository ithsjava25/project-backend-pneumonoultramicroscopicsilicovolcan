package org.example.crimearchive.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CaseNumberValidator implements ConstraintValidator<ValidCasenumber, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s.isBlank()) {
            System.out.println("String is blank returns true");
            return true;
        }
        if (s.matches("K-\\d{4}-\\d{6}$")) {
            System.out.println("String is correct, returns true");
            return true;
        }
        System.out.println("String is incorrect, returns false. String: " + s);
        return false;
    }
}
