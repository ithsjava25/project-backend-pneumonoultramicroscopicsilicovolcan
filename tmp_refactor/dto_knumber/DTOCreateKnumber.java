package org.example.crimearchive.DTO.Knumber;

import jakarta.validation.constraints.NotBlank;

public record DTOCreateKnumber (
        @NotBlank(message = "Beskrivning krävs")
        String description
) {
}
