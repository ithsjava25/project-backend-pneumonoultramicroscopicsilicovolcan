package org.example.crimearchive.evidence;

import jakarta.persistence.*;
import org.example.crimearchive.audit.Auditable;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_evidence_file_group_version",
                columnNames = {"group_id", "version"}
        ),
        indexes = {
                @Index(name = "idx_evidence_file_case_number", columnList = "case_number"),
                @Index(name = "idx_evidence_file_group_id_version", columnList = "group_id, version")
        }
)
public class EvidenceFile extends Auditable {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;
    @Column(nullable = false)
    private int version;
    @Column(name = "case_number", nullable = false)
    private String caseNumber;
    @Column(name = "s3_key_pdf", nullable = false)
    private String s3KeyPdf;
    @Column(name = "s3_key_file")
    private String s3KeyFile;
    private String originalFilename;
    private String contentType;
    @Column(nullable = false)
    private String uploadedBy;
    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    public EvidenceFile() {
    }

    public EvidenceFile(UUID id, UUID groupId, int version, String caseNumber, String s3KeyPdf,
                        String s3KeyFile, String originalFilename, String uploadedBy, LocalDateTime uploadedAt) {
        this.id = id;
        this.groupId = groupId;
        this.version = version;
        this.caseNumber = caseNumber;
        this.s3KeyPdf = s3KeyPdf;
        this.s3KeyFile = s3KeyFile;
        this.originalFilename = originalFilename;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
    }

    public UUID getId() { return id; }
    public UUID getGroupId() { return groupId; }
    public int getVersion() { return version; }
    public String getCaseNumber() { return caseNumber; }
    public String getS3KeyPdf() { return s3KeyPdf; }
    public String getS3KeyFile() { return s3KeyFile; }
    public String getOriginalFilename() { return originalFilename; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getUploadedBy() { return uploadedBy; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}
