package org.example.crimearchive.service;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.DTO.ReportResponse;
import org.example.crimearchive.bevis.Report;
import org.example.crimearchive.repository.SimpleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private SimpleRepository simpleRepository;

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private ReportService reportService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reportService, "bucket", "crime-archive");
    }


    @Test
    void saveReport_noFile_uploadsOnlyPdf() throws IOException {
        CreateReport request = new CreateReport("Johan", "Murder");

        reportService.saveReport(request, null);

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(simpleRepository, times(1)).save(any(Report.class));
    }

    @Test
    void saveReport_withImage_uploadsTwoFiles() throws IOException {
        CreateReport request = new CreateReport("Johan", "Murder");

        byte[] minimalPng = new byte[]{
                (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                0x08, 0x02, 0x00, 0x00, 0x00, (byte)0x90, 0x77, 0x53,
                (byte)0xDE, 0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41,
                0x54, 0x08, (byte)0xD7, 0x63, (byte)0xF8, (byte)0xCF, (byte)0xC0, 0x00,
                0x00, 0x00, 0x02, 0x00, 0x01, (byte)0xE2, 0x21, (byte)0xBC,
                0x33, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E,
                0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82
        };

        MockMultipartFile image = new MockMultipartFile(
                "file", "evidence.png", "image/png", minimalPng
        );

        reportService.saveReport(request, image);

        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(simpleRepository, times(1)).save(any(Report.class));
    }

    @Test
    void saveReport_withPdf_uploadsTwoFiles() throws IOException {
        CreateReport request = new CreateReport("Johan", "Murder");
        MockMultipartFile pdf = new MockMultipartFile(
                "file", "document.pdf", "application/pdf", "fake-pdf".getBytes()
        );

        reportService.saveReport(request, pdf);

        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(simpleRepository, times(1)).save(any(Report.class));
    }

    @Test
    void saveReport_withWordFile_uploadsTwoFiles() throws IOException {
        CreateReport request = new CreateReport("Johan", "Murder");
        MockMultipartFile word = new MockMultipartFile(
                "file", "report.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "fake-word".getBytes()
        );

        reportService.saveReport(request, word);

        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(simpleRepository, times(1)).save(any(Report.class));
    }

    @Test
    void saveReport_databaseFails_cleansUpS3Files() {
        CreateReport request = new CreateReport("Johan", "Murder");
        when(simpleRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        assertThrows(IOException.class, () -> reportService.saveReport(request, null));

        verify(s3Client, atLeastOnce()).deleteObject(any(DeleteObjectRequest.class));
    }


    @Test
    void downloadPdf_reportExists_returnsPdfWith200() {
        UUID uuid = UUID.randomUUID();
        Report report = new Report(uuid, "Johan", "Murder", "reports/pdf/test.pdf", null);
        when(simpleRepository.findById(uuid)).thenReturn(Optional.of(report));

        ResponseBytes<GetObjectResponse> mockBytes = mock(ResponseBytes.class);
        when(mockBytes.asByteArray()).thenReturn("pdf-content".getBytes());
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockBytes);

        ResponseEntity<byte[]> response = reportService.downloadPdf(uuid);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void downloadPdf_reportNotFound_throws404() {
        UUID uuid = UUID.randomUUID();
        when(simpleRepository.findById(uuid)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> reportService.downloadPdf(uuid)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void downloadPdf_noPdfKey_throws404() {
        UUID uuid = UUID.randomUUID();
        Report report = new Report(uuid, "Johan", "Murder", null, null);
        when(simpleRepository.findById(uuid)).thenReturn(Optional.of(report));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> reportService.downloadPdf(uuid)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }


    @Test
    void downloadFile_fileExists_returnsFileWith200() {
        UUID uuid = UUID.randomUUID();
        Report report = new Report(uuid, "Johan", "Murder", "reports/pdf/test.pdf", "reports/files/evidence.jpg");
        when(simpleRepository.findById(uuid)).thenReturn(Optional.of(report));

        ResponseBytes<GetObjectResponse> mockBytes = mock(ResponseBytes.class);
        when(mockBytes.asByteArray()).thenReturn("file-content".getBytes());
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockBytes);

        ResponseEntity<byte[]> response = reportService.downloadFile(uuid);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void downloadFile_noFileKey_throws404() {
        UUID uuid = UUID.randomUUID();
        Report report = new Report(uuid, "Johan", "Murder", "reports/pdf/test.pdf", null);
        when(simpleRepository.findById(uuid)).thenReturn(Optional.of(report));

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> reportService.downloadFile(uuid)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }


    @Test
    void getAllReportResponses_returnsListWithoutS3Keys() {
        UUID uuid = UUID.randomUUID();
        Report report = new Report(uuid, "Johan", "Murder", "reports/pdf/test.pdf", "reports/files/evidence.jpg");
        when(simpleRepository.findAll()).thenReturn(List.of(report));

        List<ReportResponse> responses = reportService.getAllReportResponses();

        assertEquals(1, responses.size());
        assertEquals(uuid, responses.get(0).uuid());
        assertEquals("Johan", responses.get(0).name());
        assertEquals("Murder", responses.get(0).event());
    }

    @Test
    void getAllReportResponses_noReports_returnsEmptyList() {
        when(simpleRepository.findAll()).thenReturn(List.of());

        List<ReportResponse> responses = reportService.getAllReportResponses();

        assertTrue(responses.isEmpty());
    }
}