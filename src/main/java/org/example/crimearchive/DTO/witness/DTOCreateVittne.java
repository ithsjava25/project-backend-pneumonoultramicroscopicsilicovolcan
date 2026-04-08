package org.example.crimearchive.dto.witness;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DTOCreateVittne(
    @NotBlank(message = "Namn krävs")
    String name,
    
    @Email(message = "Ogiltig e-post")
    String email,
    
    String phone
) {
}
