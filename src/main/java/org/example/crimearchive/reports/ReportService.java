package org.example.crimearchive.reports;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.DTO.ReportResponse;
import org.example.crimearchive.KNumberService;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.cases.CasesRepository;
import org.example.crimearchive.mapper.Mapper;
import org.example.crimearchive.polis.Account;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final KNumberService knumberService;
    private final CasesRepository casesRepository;

    public ReportService(ReportRepository reportRepository, KNumberService knumberService, CasesRepository casesRepository) {
        this.reportRepository = reportRepository;
        this.knumberService = knumberService;
        this.casesRepository = casesRepository;
    }

    @Transactional
    public String saveReport(CreateReport report, Account currentUser) {
        Cases cases;

        if (report.caseNumber() == null || report.caseNumber().isBlank()) {
            String newCaseNumber = knumberService.getKNumber();
            cases = new Cases(newCaseNumber);
            if (currentUser != null) cases.getAccounts().add(currentUser);
            casesRepository.save(cases);
        } else {
            String sanitized = caseNumberSanitation(report.caseNumber());
            cases = casesRepository.findFirstByCaseNumber(sanitized)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found: " + sanitized));
        }

        Report newReport = new Report(
                UUID.randomUUID(),
                report.name(),
                report.event(),
                report.witness(),
                report.victim(),
                cases
        );
        reportRepository.save(newReport);
        return cases.getCaseNumber();
    }

    private String caseNumberSanitation(String caseNumber) {
        if (caseNumber.matches("^\\d{4}-\\d{6}$")) {
            return "K-" + caseNumber;
        } else {
            return caseNumber.toUpperCase();
        }
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public long getAmount() {
        return reportRepository.count();
    }

    public List<ReportResponse> getAllReportResponses() {
        return reportRepository.findAll().stream()
                .map(r -> new ReportResponse(r.getUuid(), r.getName(), r.getEvent()))
                .toList();
    }
}
