package org.example.crimearchive.dto.defense;


import java.time.LocalDate;
import java.time.ZonedDateTime;

public record DTOAdvocat(
        long id,
        String email,
        String company,
        String telephone,
        String name,
        String KNumber,
        LocalDate appointedTime,
        ZonedDateTime crimeTime
) {
}
