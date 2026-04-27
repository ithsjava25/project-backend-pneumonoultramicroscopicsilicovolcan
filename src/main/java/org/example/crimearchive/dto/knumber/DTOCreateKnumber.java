package org.example.crimearchive.dto.knumber;

import jakarta.validation.constraints.NotBlank;

public record DTOCreateKnumber(
        @NotBlank(message = "Beskrivning krävs")
        String description
) {
}
