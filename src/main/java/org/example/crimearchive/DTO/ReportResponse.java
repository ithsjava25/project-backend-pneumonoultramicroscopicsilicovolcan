package org.example.crimearchive.dto;

import java.util.UUID;

public record ReportResponse(
        UUID uuid,
        String name,
        String event
) {}