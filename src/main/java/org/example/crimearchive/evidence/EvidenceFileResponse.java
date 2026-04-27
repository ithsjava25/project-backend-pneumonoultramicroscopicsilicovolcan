package org.example.crimearchive.evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public record EvidenceFileResponse(
        UUID id,
        UUID groupId,
        int version,
        String caseNumber,
        String originalFilename,
        String uploadedBy,
        LocalDateTime uploadedAt
) {
    public static EvidenceFileResponse from(EvidenceFile e) {
        return new EvidenceFileResponse(
                e.getId(), e.getGroupId(), e.getVersion(), e.getCaseNumber(),
                e.getOriginalFilename(), e.getUploadedBy(), e.getUploadedAt()
        );
    }
}
