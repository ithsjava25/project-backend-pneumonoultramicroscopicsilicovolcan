package org.example.crimearchive.dto.defense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public record DTOCreateAdvocat(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Company is required")
        String company,
        @NotBlank(message = "Phone is required")
        String phone,
        @NotNull(message = "Appointed time is required")
        LocalDate appointedTime,
        @NotNull(message = "Crime time is required")
        ZonedDateTime crimeTime
) {
}
