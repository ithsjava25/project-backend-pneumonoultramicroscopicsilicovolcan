package org.example.crimearchive.evidence;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class EvidenceFile {

    @Id
    private UUID id;

    private UUID groupId;
    private int version;
    private String caseNumber;
    private String s3KeyPdf;
    private String s3KeyFile;
    private String originalFilename;
    private String uploadedBy;
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
    public String getUploadedBy() { return uploadedBy; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}
