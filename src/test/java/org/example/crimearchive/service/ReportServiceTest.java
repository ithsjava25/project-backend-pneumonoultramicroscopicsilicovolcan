package org.example.crimearchive.service;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.DTO.ReportResponse;
import org.example.crimearchive.KNumberService;
import org.example.crimearchive.cases.CaseLifecycleService;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.cases.CasesRepository;
import org.example.crimearchive.reports.Report;
import org.example.crimearchive.reports.ReportRepository;
import org.example.crimearchive.reports.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private KNumberService kNumberService;
    @Mock
    private CasesRepository casesRepository;
    @Mock
    private CaseLifecycleService lifecycleService;

    @InjectMocks
    private ReportService reportService;

    @Test
    void saveReport_noCaseNumber_createsNewCaseAndSavesReport() {
        CreateReport request = new CreateReport("Murder", "Johan");
        when(kNumberService.getKNumber()).thenReturn("K-2026-000001");

        String caseNumber = reportService.saveReport(request, null);

        assertEquals("K-2026-000001", caseNumber);
        verify(casesRepository, times(1)).save(any(Cases.class));
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(lifecycleService).initCase(any(Cases.class), eq("system"));
    }

    @Test
    void saveReport_existingCaseNumber_usesExistingCase() {
        Cases existingCase = new Cases("K-2026-000001");
        CreateReport request = new CreateReport("Murder", "Johan", "K-2026-000001");
        when(casesRepository.findFirstByCaseNumber("K-2026-000001")).thenReturn(Optional.of(existingCase));

        String caseNumber = reportService.saveReport(request, null);

        assertEquals("K-2026-000001", caseNumber);
        verify(casesRepository, never()).save(any());
        verify(reportRepository, times(1)).save(any(Report.class));
        verify(lifecycleService, never()).initCase(any(), any());
    }

    @Test
    void saveReport_caseNotFound_throws404() {
        CreateReport request = new CreateReport("Murder", "Johan", "K-2026-999999");
        when(casesRepository.findFirstByCaseNumber("K-2026-999999")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> reportService.saveReport(request, null)
        );
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void getAllReportResponses_returnsCorrectData() {
        UUID uuid = UUID.randomUUID();
        Report report = new Report(uuid, "Johan", "Murder");
        when(reportRepository.findAll()).thenReturn(List.of(report));

        List<ReportResponse> responses = reportService.getAllReportResponses();

        assertEquals(1, responses.size());
        assertEquals(uuid, responses.get(0).uuid());
        assertEquals("Johan", responses.get(0).name());
        assertEquals("Murder", responses.get(0).event());
    }

    @Test
    void getAllReportResponses_noReports_returnsEmptyList() {
        when(reportRepository.findAll()).thenReturn(List.of());

        List<ReportResponse> responses = reportService.getAllReportResponses();

        assertTrue(responses.isEmpty());
    }

    @Test
    void getAmount_returnsRepositoryCount() {
        when(reportRepository.count()).thenReturn(5L);

        assertEquals(5L, reportService.getAmount());
    }
}
