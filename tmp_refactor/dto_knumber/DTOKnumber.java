package org.example.crimearchive.DTO.Knumber;

import java.time.ZonedDateTime;

public record DTOKnumber(
        long id,
        String KNumber,        // Själva ärendenumret (t.ex. K-2026-000001)
        String description,    // Beskrivning av ärendet/brottet
        ZonedDateTime createdAt // När ärendet skapades
) {
}
