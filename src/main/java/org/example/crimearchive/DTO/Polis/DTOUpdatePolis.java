package org.example.crimearchive.dto.Polis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DTOUpdatePolis(
        Long id,
        @NotBlank(message = "Namn får inte vara tomt")
        String fullName,
        @NotBlank(message = "Yrke får inte vara tomt")
        String profession,
        @NotBlank(message = "Avdelning får inte vara tomt")
        String department,
        @NotBlank(message = "Användarnamn får inte vara tomt")
        String username,
        String password,
        String roles
) {
}
