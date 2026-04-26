package org.example.crimearchive.dto.police;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DTOUpdateProfile(
        @NotBlank(message = "Namn kan inte vara tomt")
        String fullname,
        String password,
        Long id) {
}
