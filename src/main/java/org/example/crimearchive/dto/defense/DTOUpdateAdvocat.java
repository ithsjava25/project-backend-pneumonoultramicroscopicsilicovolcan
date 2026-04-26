package org.example.crimearchive.dto.defense;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public record DTOUpdateAdvocat(
        String name,
        String email,
        String company,
        String phone,
        String KNumber,
        LocalDate appointedTime,
        ZonedDateTime crimeTime
) {
}
