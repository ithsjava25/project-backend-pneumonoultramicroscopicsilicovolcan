package org.example.crimearchive.DTO;

import java.util.UUID;

public record ReportResponse(
        UUID uuid,
        String name,
        String event
) {}