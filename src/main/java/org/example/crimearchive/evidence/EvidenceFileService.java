package org.example.crimearchive.evidence;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EvidenceFileService {

    private final EvidenceFileRepository evidenceFileRepository;
    private final S3Client s3Client;

    @Value("${minio.bucket}")
    private String bucket;

    public EvidenceFileService(EvidenceFileRepository evidenceFileRepository, S3Client s3Client) {
        this.evidenceFileRepository = evidenceFileRepository;
        this.s3Client = s3Client;
    }

    public void upload(String caseNumber, String reportName, String reportEvent,
                       MultipartFile file, String uploadedBy) throws IOException {
        upload(caseNumber, reportName, reportEvent, file, uploadedBy, null);
    }

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

            evidenceFileRepository.save(new EvidenceFile(
                    UUID.randomUUID(),
                    resolvedGroupId,
                    nextVersion,
                    caseNumber,
                    s3KeyPdf,
                    s3KeyFile,
                    file != null ? file.getOriginalFilename() : null,
                    uploadedBy,
                    LocalDateTime.now()
            ));

        } catch (Exception e) {
            deleteIfExists(s3KeyPdf);
            deleteIfExists(s3KeyFile);
            throw new IOException("Kunde inte ladda upp bevisfil: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<byte[]> downloadPdf(UUID id) {
        EvidenceFile evidence = evidenceFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bevisfil hittades inte: " + id));

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(evidence.getS3KeyPdf())
                        .build()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=rapport.pdf")
                .body(objectBytes.asByteArray());
    }

    public ResponseEntity<byte[]> downloadFile(UUID id) {
        EvidenceFile evidence = evidenceFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bevisfil hittades inte: " + id));

        if (evidence.getS3KeyFile() == null) {
            throw new RuntimeException("Ingen bifogad fil finns för denna post");
        }

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(evidence.getS3KeyFile())
                        .build()
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + evidence.getOriginalFilename())
                .body(objectBytes.asByteArray());
    }

    public List<EvidenceFile> getByCaseNumber(String caseNumber) {
        return evidenceFileRepository.findByCaseNumber(caseNumber);
    }

    public List<EvidenceFile> getLatestVersionsByCaseNumber(String caseNumber) {
        return evidenceFileRepository.findLatestVersionsByCaseNumber(caseNumber);
    }

    public List<EvidenceFile> getVersionHistory(UUID groupId) {
        return evidenceFileRepository.findByGroupIdOrderByVersionAsc(groupId);
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
