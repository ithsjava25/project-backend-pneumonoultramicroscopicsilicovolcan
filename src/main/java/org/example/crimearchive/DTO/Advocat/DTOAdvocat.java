package org.example.crimearchive.DTO.Advocat;


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
