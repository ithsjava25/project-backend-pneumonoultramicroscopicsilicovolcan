package org.example.crimearchive.DTO.Polis;

import jakarta.validation.constraints.NotBlank;

public record DTOCreatePolis(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Badge number is required")
        String badgeNumber,
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Phone is required")
        String phone
) {
}
