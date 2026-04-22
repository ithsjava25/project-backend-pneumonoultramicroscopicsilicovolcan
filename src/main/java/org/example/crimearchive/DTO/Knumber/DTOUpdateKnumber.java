package org.example.crimearchive.dto.knumber;

import jakarta.validation.constraints.Size;

public record DTOUpdateKnumber(
        @Size(min = 1, message = "Beskrivning får inte vara tom om den anges")
        String description
) {
}
