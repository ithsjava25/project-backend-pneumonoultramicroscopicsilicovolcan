package org.example.crimearchive.cases;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_event")
public class CaseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Cases caseEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseEventType eventType;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String performedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public CaseEvent() {
    }

    public CaseEvent(Cases caseEntity, CaseEventType eventType, String description, String performedBy) {
        this.caseEntity = caseEntity;
        this.eventType = eventType;
        this.description = description;
        this.performedBy = performedBy;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Cases getCaseEntity() {
        return caseEntity;
    }

    public CaseEventType getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
