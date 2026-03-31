package org.example.crimearchive.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.bevis.Report;
import org.example.crimearchive.mapper.ReportMapper;
import org.example.crimearchive.repository.SimpleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        String s3Key = null;

        // Ladda upp till MinIO om en fil bifogades
        if (file != null && !file.isEmpty()) {
            s3Key = "reports/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.putObject(bucket, s3Key, file.getInputStream(), metadata);
        }

        simpleRepository.save(ReportMapper.toEntity(report, s3Key));
    }

    public List<Report> getAllReports() {
        return simpleRepository.findAll();
    }
}