package org.example.crimearchive.dto.Åklagare;

import jakarta.validation.constraints.NotBlank;

public record DTOCreateÅklagare(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Phone is required")
        String phone,
        @NotBlank(message = "Company is required")
        String company,
        @NotBlank(message = "KNumber is required")
        String KNumber
) {
}
