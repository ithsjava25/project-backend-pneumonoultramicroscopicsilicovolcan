package org.example.crimearchive.DTO.Golare;

import jakarta.validation.constraints.NotBlank;

public record DTOCreateGolare(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Phone is required")
        String phone
) {
}
