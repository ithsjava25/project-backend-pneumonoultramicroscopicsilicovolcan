package org.example.crimearchive.cases;

import java.time.LocalDateTime;

public record CaseCommentResponse(Long id, String content, String author, LocalDateTime createdAt) {
    static CaseCommentResponse from(CaseComment c) {
        return new CaseCommentResponse(c.getId(), c.getContent(), c.getAuthor(), c.getCreatedAt());
    }
}
