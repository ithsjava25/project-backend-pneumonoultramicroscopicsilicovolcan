package org.example.crimearchive.evidence;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.crimearchive.cases.CaseLifecycleService;
import org.example.crimearchive.cases.CasesRepository;
import org.example.crimearchive.permissions.PermissionService;
import org.example.crimearchive.polis.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EvidenceFileService {

    private final EvidenceFileRepository evidenceFileRepository;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final PermissionService permissionService;
    private final CaseLifecycleService lifecycleService;
    private final CasesRepository casesRepository;

    @Value("${minio.bucket}")
    private String bucket;

    public EvidenceFileService(EvidenceFileRepository evidenceFileRepository,
                               S3Client s3Client,
                               S3Presigner s3Presigner,
                               PermissionService permissionService,
                               CaseLifecycleService lifecycleService,
                               CasesRepository casesRepository) {
        this.evidenceFileRepository = evidenceFileRepository;
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.permissionService = permissionService;
        this.lifecycleService = lifecycleService;
        this.casesRepository = casesRepository;
    }

    public void upload(String caseNumber, String reportName, String reportEvent,
                       MultipartFile file, String uploadedBy) throws IOException {
        upload(caseNumber, reportName, reportEvent, file, uploadedBy, null);
    }

    @Transactional
    public void upload(String caseNumber, String reportName, String reportEvent,
                       MultipartFile file, String uploadedBy, UUID groupId) throws IOException {
        String s3KeyPdf = null;
        String s3KeyFile = null;

        try {
            int nextVersion = 1;
            UUID resolvedGroupId = groupId;

            if (resolvedGroupId != null) {
                int latest = evidenceFileRepository.findTopByGroupIdOrderByVersionDesc(resolvedGroupId)
                        .map(EvidenceFile::getVersion)
                        .orElse(0);
                nextVersion = latest + 1;
            } else {
                resolvedGroupId = UUID.randomUUID();
            }

            byte[] pdfBytes = generatePdf(reportName, reportEvent, file);
            s3KeyPdf = "cases/" + caseNumber + "/pdf/" + UUID.randomUUID() + ".pdf";

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(s3KeyPdf)
                            .contentType("application/pdf")
                            .build(),
                    RequestBody.fromBytes(pdfBytes)
            );

            if (file != null && !file.isEmpty()) {
                s3KeyFile = "cases/" + caseNumber + "/files/" + UUID.randomUUID();

                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(s3KeyFile)
                                .contentType(file.getContentType())
                                .build(),
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize())
                );
            }

            EvidenceFile evidenceFile = new EvidenceFile(
                    UUID.randomUUID(),
                    resolvedGroupId,
                    nextVersion,
                    caseNumber,
                    s3KeyPdf,
                    s3KeyFile,
                    file != null ? file.getOriginalFilename() : null,
                    uploadedBy,
                    LocalDateTime.now()
            );
            evidenceFile.setContentType(file != null ? file.getContentType() : null);
            evidenceFileRepository.save(evidenceFile);

            casesRepository.findFirstByCaseNumber(caseNumber).ifPresent(cases ->
                    lifecycleService.onDocumentUploaded(cases,
                            file != null ? file.getOriginalFilename() : "okänd fil",
                            uploadedBy));

        } catch (Exception e) {
            deleteIfExists(s3KeyPdf);
            deleteIfExists(s3KeyFile);
            throw new IOException("Kunde inte ladda upp bevisfil: " + e.getMessage(), e);
        }
    }

    public String signedPdfUrl(UUID id, Account currentUser) {
        EvidenceFile evidence = evidenceFileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bevisfil hittades inte: " + id));
        requireCaseAccess(evidence.getCaseNumber(), currentUser);
        return presign(evidence.getS3KeyPdf());
    }

    public String signedFileUrl(UUID id, Account currentUser) {
        EvidenceFile evidence = evidenceFileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bevisfil hittades inte: " + id));
        requireCaseAccess(evidence.getCaseNumber(), currentUser);
        if (evidence.getS3KeyFile() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingen bifogad fil finns för denna post");
        }
        return presign(evidence.getS3KeyFile());
    }

    private String presign(String s3Key) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(s3Key)
                        .build())
                .build();
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public List<EvidenceFile> getByCaseNumber(String caseNumber) {
        return evidenceFileRepository.findByCaseNumber(caseNumber);
    }

    public List<EvidenceFile> getLatestVersionsByCaseNumber(String caseNumber) {
        return evidenceFileRepository.findLatestVersionsByCaseNumber(caseNumber);
    }

    public List<EvidenceFile> getVersionHistory(UUID groupId, Account currentUser) {
        List<EvidenceFile> history = evidenceFileRepository.findByGroupIdOrderByVersionAsc(groupId);
        if (!history.isEmpty()) {
            requireCaseAccess(history.get(0).getCaseNumber(), currentUser);
        }
        return history;
    }

    @Transactional
    public void deleteGroup(UUID groupId, Account currentUser) {
        List<EvidenceFile> files = evidenceFileRepository.findByGroupIdOrderByVersionAsc(groupId);
        if (files.isEmpty()) return;
        requireCaseAccess(files.get(0).getCaseNumber(), currentUser);
        for (EvidenceFile file : files) {
            deleteIfExists(file.getS3KeyPdf());
            deleteIfExists(file.getS3KeyFile());
        }
        evidenceFileRepository.deleteAll(files);
    }

    private void requireCaseAccess(String caseNumber, Account currentUser) {
        if (!permissionService.canAccessCase(caseNumber, currentUser)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Åtkomst nekad till ärende: " + caseNumber);
        }
    }

    private byte[] generatePdf(String reportName, String reportEvent, MultipartFile file) throws Exception {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, pdfStream);
        document.open();

        document.add(new Paragraph("Brottsanmälan"));
        document.add(new Paragraph("Namn: " + reportName));
        document.add(new Paragraph("Brottstyp: " + reportEvent));
        document.add(new Paragraph("Datum: " + LocalDateTime.now()));

        if (file != null && !file.isEmpty()) {
            document.newPage();
            if (isImage(file)) {
                document.add(new Paragraph("Bifogat bevisfoto:"));
                Image image = Image.getInstance(file.getBytes());
                image.scaleToFit(500, 700);
                document.add(image);
            } else if (isPdf(file)) {
                document.add(new Paragraph("Bifogat dokument (PDF): " + file.getOriginalFilename()));
            } else if (isWord(file)) {
                document.add(new Paragraph("Bifogat dokument (Word): " + file.getOriginalFilename()));
            } else {
                document.add(new Paragraph("Bifogad fil: " + file.getOriginalFilename()));
            }
        }

        document.close();
        return pdfStream.toByteArray();
    }

    private void deleteIfExists(String key) {
        if (key == null) return;
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (Exception ignored) {
        }
    }

    private boolean isImage(MultipartFile file) {
        String ct = file.getContentType();
        return ct != null && ct.startsWith("image/");
    }

    private boolean isPdf(MultipartFile file) {
        String ct = file.getContentType();
        return "application/pdf".equals(ct);
    }

    private boolean isWord(MultipartFile file) {
        String ct = file.getContentType();
        return ct != null && (ct.equals("application/msword") ||
                ct.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    }
}
