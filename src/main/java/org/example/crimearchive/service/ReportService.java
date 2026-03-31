package org.example.crimearchive.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    private final SimpleRepository simpleRepository;
    private final AmazonS3 s3Client;

    @Value("${minio.bucket}")
    private String bucket;

    public ReportService(SimpleRepository simpleRepository, AmazonS3 s3Client) {
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

            ObjectMetadata pdfMetadata = new ObjectMetadata();
            pdfMetadata.setContentType("application/pdf");
            pdfMetadata.setContentLength(pdfBytes.length);

            s3Client.putObject(bucket, s3KeyPdf, new ByteArrayInputStream(pdfBytes), pdfMetadata);

        } catch (Exception e) {
            throw new IOException("Kunde inte generera PDF: " + e.getMessage());
        }

        String s3KeyFile = null;
        if (file != null && !file.isEmpty()) {
            s3KeyFile = "reports/files/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            ObjectMetadata fileMetadata = new ObjectMetadata();
            fileMetadata.setContentType(file.getContentType());
            fileMetadata.setContentLength(file.getSize());

            s3Client.putObject(bucket, s3KeyFile, file.getInputStream(), fileMetadata);
        }

        simpleRepository.save(ReportMapper.toEntity(report, s3KeyPdf, s3KeyFile));
    }

    public ResponseEntity<byte[]> downloadPdf(UUID uuid) throws IOException {
        Report report = simpleRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Rapporten hittades inte: " + uuid));

        if (report.getS3KeyPdf() == null) {
            throw new RuntimeException("Ingen PDF finns för denna rapport");
        }

        S3Object s3Object = s3Client.getObject(bucket, report.getS3KeyPdf());
        byte[] data = s3Object.getObjectContent().readAllBytes();

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

        S3Object s3Object = s3Client.getObject(bucket, report.getS3KeyFile());
        byte[] data = s3Object.getObjectContent().readAllBytes();

        String filename = report.getS3KeyFile().substring(report.getS3KeyFile().lastIndexOf("/") + 1);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(data);
    }

    public List<Report> getAllReports() {
        return simpleRepository.findAll();
    }
}