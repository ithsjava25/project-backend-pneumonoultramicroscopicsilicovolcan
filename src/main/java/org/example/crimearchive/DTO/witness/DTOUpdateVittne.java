package org.example.crimearchive.dto.witness;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record DTOUpdateVittne(
    @Size(min = 1, message = "Namn får inte vara tomt om det anges")
    String name,
    @Email(message = "Ogiltig e-post")
    String email,
    @Size(min = 1, message = "Telefon får inte vara tom om den anges")
    String phone
) {
}
