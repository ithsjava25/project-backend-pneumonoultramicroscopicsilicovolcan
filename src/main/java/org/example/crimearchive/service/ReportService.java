package org.example.crimearchive.service;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    private final SimpleRepository simpleRepository;
    private final S3Client s3Client; // Ändrat från AmazonS3

    @Value("${minio.bucket}")
    private String bucket;

    public ReportService(SimpleRepository simpleRepository, S3Client s3Client) {
        this.simpleRepository = simpleRepository;
        this.s3Client = s3Client;
    }

    public void saveReport(CreateReport report, MultipartFile file) throws IOException {

        String s3KeyPdf = null;
        try {
            ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, pdfStream);
            document.open();
            document.add(new Paragraph("Brottsanmälan"));
            document.add(new Paragraph("Namn: " + report.name()));
            document.add(new Paragraph("Brottstyp: " + report.event()));
            document.add(new Paragraph("Datum: " + LocalDateTime.now()));
            document.close();

            byte[] pdfBytes = pdfStream.toByteArray();
            s3KeyPdf = "reports/pdf/" + UUID.randomUUID() + "_" + report.name() + ".pdf";

            // AWS SDK v2 PutObject
            PutObjectRequest putPdf = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3KeyPdf)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(putPdf, RequestBody.fromBytes(pdfBytes));

        } catch (Exception e) {
            throw new IOException("Kunde inte generera PDF: " + e.getMessage());
        }

        String s3KeyFile = null;
        if (file != null && !file.isEmpty()) {
            s3KeyFile = "reports/files/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            // AWS SDK v2 PutObject för MultipartFile
            PutObjectRequest putFile = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3KeyFile)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putFile, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        }

        simpleRepository.save(ReportMapper.toEntity(report, s3KeyPdf, s3KeyFile));
    }

    public ResponseEntity<byte[]> downloadPdf(UUID uuid) throws IOException {
        Report report = simpleRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Rapporten hittades inte: " + uuid));

        if (report.getS3KeyPdf() == null) {
            throw new RuntimeException("Ingen PDF finns för denna rapport");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(report.getS3KeyPdf())
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
        byte[] data = objectBytes.asByteArray();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=rapport.pdf")
                .body(data);
    }

    public ResponseEntity<byte[]> downloadFile(UUID uuid) throws IOException {
        Report report = simpleRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Rapporten hittades inte: " + uuid));

        if (report.getS3KeyFile() == null) {
            throw new RuntimeException("Ingen bifogad fil finns för denna rapport");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(report.getS3KeyFile())
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
        byte[] data = objectBytes.asByteArray();

        String filename = report.getS3KeyFile().substring(report.getS3KeyFile().lastIndexOf("/") + 1);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(data);
    }

    public List<Report> getAllReports() {
        return simpleRepository.findAll();
    }
}