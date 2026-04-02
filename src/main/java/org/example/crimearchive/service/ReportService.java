package org.example.crimearchive.service;

import com.itextpdf.text.Image;
import org.example.crimearchive.DTO.ReportResponse;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.bevis.Report;
import org.example.crimearchive.mapper.ReportMapper;
import org.example.crimearchive.repository.SimpleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    private final SimpleRepository simpleRepository;
    private final S3Client s3Client;

    @Value("${minio.bucket}")
    private String bucket;

    public ReportService(SimpleRepository simpleRepository, S3Client s3Client) {
        this.simpleRepository = simpleRepository;
        this.s3Client = s3Client;
    }

    @Transactional
    public void saveReport(CreateReport report, MultipartFile file) throws IOException {
        String s3KeyPdf = null;
        String s3KeyFile = null;

        try {
            ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, pdfStream);
            document.open();


            document.add(new Paragraph("Brottsanmälan"));
            document.add(new Paragraph("Namn: " + report.name()));
            document.add(new Paragraph("Brottstyp: " + report.event()));
            document.add(new Paragraph("Datum: " + LocalDateTime.now()));


            if (file != null && !file.isEmpty()) {
                document.newPage();

                if (isImage(file)) {
                    document.add(new Paragraph("Bifogat bevisfoto:"));
                    Image image = Image.getInstance(file.getBytes());
                    image.scaleToFit(500, 700);
                    document.add(image);

                } else if (isPdf(file)) {
                    document.add(new Paragraph("Bifogat dokument (PDF):"));
                    document.add(new Paragraph(file.getOriginalFilename()));
                    document.add(new Paragraph("Se separat bifogad fil för fullständigt dokument."));

                } else if (isWord(file)) {
                    document.add(new Paragraph("Bifogat dokument (Word):"));
                    document.add(new Paragraph(file.getOriginalFilename()));
                    document.add(new Paragraph("Se separat bifogad fil för fullständigt dokument."));

                } else {
                    document.add(new Paragraph("Bifogad fil: " + file.getOriginalFilename()));
                }
            }

            document.close();

            byte[] pdfBytes = pdfStream.toByteArray();
            s3KeyPdf = "reports/pdf/" + UUID.randomUUID() + ".pdf";

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(s3KeyPdf)
                            .contentType("application/pdf")
                            .build(),
                    RequestBody.fromBytes(pdfBytes)
            );

            if (file != null && !file.isEmpty()) {
                s3KeyFile = "reports/files/" + UUID.randomUUID();

                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(s3KeyFile)
                                .contentType(file.getContentType())
                                .build(),
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize())
                );
            }

            simpleRepository.save(ReportMapper.toEntity(report, s3KeyPdf, s3KeyFile));

        } catch (Exception e) {
            try {
                if (s3KeyPdf != null) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucket).key(s3KeyPdf).build());
                }
            } catch (Exception cleanupEx) {}
            try {
                if (s3KeyFile != null) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucket).key(s3KeyFile).build());
                }
            } catch (Exception cleanupEx) {}
            throw new IOException("Kunde inte spara rapport: " + e.getMessage());
        }
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private boolean isPdf(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }

    private boolean isWord(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
    }

    public ResponseEntity<byte[]> downloadPdf(UUID uuid) {
        Report report = simpleRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rapporten hittades inte: " + uuid));

        if (report.getS3KeyPdf() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingen PDF finns för denna rapport");
        }

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(report.getS3KeyPdf())
                        .build()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=rapport.pdf")
                .body(objectBytes.asByteArray());
    }

    public ResponseEntity<byte[]> downloadFile(UUID uuid) {
        Report report = simpleRepository.findById(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Rapporten hittades inte: " + uuid));

        if (report.getS3KeyFile() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ingen bifogad fil finns för denna rapport");
        }

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(report.getS3KeyFile())
                        .build()
        );

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=fil")
                .body(objectBytes.asByteArray());
    }
    public List<ReportResponse> getAllReportResponses() {
        return simpleRepository.findAll().stream()
                .map(r -> new ReportResponse(r.getUuid(), r.getName(), r.getEvent()))
                .toList();
    }

    public List<Report> getAllReports() {
        return simpleRepository.findAll();
    }
}