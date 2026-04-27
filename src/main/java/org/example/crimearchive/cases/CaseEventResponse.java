package org.example.crimearchive.cases;

import java.time.LocalDateTime;

public record CaseEventResponse(Long id, CaseEventType eventType, String description,
                                String performedBy, LocalDateTime createdAt) {
    static CaseEventResponse from(CaseEvent e) {
        return new CaseEventResponse(e.getId(), e.getEventType(), e.getDescription(),
                e.getPerformedBy(), e.getCreatedAt());
    }
}
