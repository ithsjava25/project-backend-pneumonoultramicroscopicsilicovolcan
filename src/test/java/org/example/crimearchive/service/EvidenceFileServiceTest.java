package org.example.crimearchive.service;

import org.example.crimearchive.evidence.EvidenceFile;
import org.example.crimearchive.evidence.EvidenceFileRepository;
import org.example.crimearchive.evidence.EvidenceFileService;
import org.example.crimearchive.permissions.PermissionService;
import org.example.crimearchive.polis.Account;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvidenceFileServiceTest {

    @Mock
    private EvidenceFileRepository evidenceFileRepository;
    @Mock
    private S3Client s3Client;
    @Mock
    private PermissionService permissionService;
    @Mock
    private Account mockUser;

    @InjectMocks
    private EvidenceFileService evidenceFileService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(evidenceFileService, "bucket", "crime-archive");
    }

    @Test
    void upload_withImage_uploadsTwoFiles() throws IOException {
        byte[] minimalPng = new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                0x08, 0x02, 0x00, 0x00, 0x00, (byte) 0x90, 0x77, 0x53,
                (byte) 0xDE, 0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41,
                0x54, 0x08, (byte) 0xD7, 0x63, (byte) 0xF8, (byte) 0xCF, (byte) 0xC0, 0x00,
                0x00, 0x00, 0x02, 0x00, 0x01, (byte) 0xE2, 0x21, (byte) 0xBC,
                0x33, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E,
                0x44, (byte) 0xAE, 0x42, 0x60, (byte) 0x82
        };
        MockMultipartFile image = new MockMultipartFile("file", "evidence.png", "image/png", minimalPng);

        evidenceFileService.upload("K-2026-000001", "Johan", "Murder", image, "officer1");

        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(evidenceFileRepository, times(1)).save(any(EvidenceFile.class));
    }

    @Test
    void upload_withPdf_uploadsTwoFiles() throws IOException {
        MockMultipartFile pdf = new MockMultipartFile("file", "document.pdf", "application/pdf", "fake-pdf".getBytes());

        evidenceFileService.upload("K-2026-000001", "Johan", "Murder", pdf, "officer1");

        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(evidenceFileRepository, times(1)).save(any(EvidenceFile.class));
    }

    @Test
    void upload_withWordFile_uploadsTwoFiles() throws IOException {
        MockMultipartFile word = new MockMultipartFile(
                "file", "report.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "fake-word".getBytes()
        );

        evidenceFileService.upload("K-2026-000001", "Johan", "Murder", word, "officer1");

        verify(s3Client, times(2)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(evidenceFileRepository, times(1)).save(any(EvidenceFile.class));
    }

    @Test
    void upload_noFile_uploadsOnlyPdf() throws IOException {
        evidenceFileService.upload("K-2026-000001", "Johan", "Murder", null, "officer1");

        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
        verify(evidenceFileRepository, times(1)).save(any(EvidenceFile.class));
    }

    @Test
    void upload_databaseFails_cleansUpS3Files() {
        when(evidenceFileRepository.save(any())).thenThrow(new RuntimeException("Database error"));

        assertThrows(IOException.class, () ->
                evidenceFileService.upload("K-2026-000001", "Johan", "Murder", null, "officer1")
        );

        verify(s3Client, atLeastOnce()).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void downloadPdf_exists_returnsPdfWith200() {
        UUID id = UUID.randomUUID();
        EvidenceFile ev = new EvidenceFile(id, UUID.randomUUID(), 1, "K-2026-000001", "cases/K-2026-000001/pdf/test.pdf", null, null, "officer1", null);
        when(evidenceFileRepository.findById(id)).thenReturn(Optional.of(ev));
        when(permissionService.canAccessCase(anyString(), any())).thenReturn(true);

        ResponseBytes<GetObjectResponse> mockBytes = mock(ResponseBytes.class);
        when(mockBytes.asByteArray()).thenReturn("pdf-content".getBytes());
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockBytes);

        ResponseEntity<byte[]> response = evidenceFileService.downloadPdf(id, mockUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void downloadPdf_notFound_throwsException() {
        UUID id = UUID.randomUUID();
        when(evidenceFileRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> evidenceFileService.downloadPdf(id, mockUser));
    }

    @Test
    void downloadPdf_forbidden_throwsException() {
        UUID id = UUID.randomUUID();
        EvidenceFile ev = new EvidenceFile(id, UUID.randomUUID(), 1, "K-2026-000001", "cases/K-2026-000001/pdf/test.pdf", null, null, "officer1", null);
        when(evidenceFileRepository.findById(id)).thenReturn(Optional.of(ev));
        when(permissionService.canAccessCase(anyString(), any())).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> evidenceFileService.downloadPdf(id, mockUser));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void downloadFile_exists_returnsWith200() {
        UUID id = UUID.randomUUID();
        EvidenceFile ev = new EvidenceFile(id, UUID.randomUUID(), 1, "K-2026-000001",
                "cases/K-2026-000001/pdf/test.pdf", "cases/K-2026-000001/files/evidence.jpg",
                "evidence.jpg", "officer1", null);
        when(evidenceFileRepository.findById(id)).thenReturn(Optional.of(ev));
        when(permissionService.canAccessCase(anyString(), any())).thenReturn(true);

        ResponseBytes<GetObjectResponse> mockBytes = mock(ResponseBytes.class);
        when(mockBytes.asByteArray()).thenReturn("file-content".getBytes());
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(mockBytes);

        ResponseEntity<byte[]> response = evidenceFileService.downloadFile(id, mockUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void downloadFile_noFileKey_throwsException() {
        UUID id = UUID.randomUUID();
        EvidenceFile ev = new EvidenceFile(id, UUID.randomUUID(), 1, "K-2026-000001",
                "cases/K-2026-000001/pdf/test.pdf", null, null, "officer1", null);
        when(evidenceFileRepository.findById(id)).thenReturn(Optional.of(ev));
        when(permissionService.canAccessCase(anyString(), any())).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> evidenceFileService.downloadFile(id, mockUser));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
