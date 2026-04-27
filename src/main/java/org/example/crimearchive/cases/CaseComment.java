package org.example.crimearchive.cases;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_comment")
public class CaseComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Cases caseEntity;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public CaseComment() {
    }

    public CaseComment(Cases caseEntity, String content, String author) {
        this.caseEntity = caseEntity;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Cases getCaseEntity() {
        return caseEntity;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
