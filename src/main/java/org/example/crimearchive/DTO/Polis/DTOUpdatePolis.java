package org.example.crimearchive.DTO.Polis;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record DTOUpdatePolis(
        Long id,
        @NotBlank(message = "Namn får inte vara tomt")
        String fullName,
        @NotBlank(message = "Yrke får inte vara tomt")
        String profession,
        @NotBlank(message = "Avdelning får inte vara tomt")
        String department,
        @NotBlank
        String username,
        @NotBlank(message = "Lösenordet får inte vara tomt")
        String password,

        List<String> roles
) {
}
