package org.example.crimearchive.reports;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.KNumberService;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.cases.CasesRepository;
import org.example.crimearchive.polis.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final KNumberService knumberService;
    private final CasesRepository casesRepository;

    public ReportService(ReportRepository reportRepository, KNumberService kservice, CasesRepository casesRepository) {
        this.reportRepository = reportRepository;
        this.knumberService = kservice;
        this.casesRepository = casesRepository;
    }

    @Transactional
    public void saveReport(CreateReport report, Account currentUser) {
        Cases cases;

        if (report.caseNumber() == null || report.caseNumber().isBlank()) {
            Cases newCase = new Cases(getNextcaseNumber());
            casesRepository.save(newCase);
            reportRepository.save(newCase.addReport(report));
        } else {
            if (casesRepository.existsByCaseNumber(report.caseNumber())) {
                Optional<Cases> oldCase = casesRepository.findFirstByCaseNumber(report.caseNumber());
                reportRepository.save(oldCase.get().addReport(report));
            } else {
                throw new RuntimeException("Wrong case number");
            }
            String newCaseNumber = knumberService.getKNumber();
            cases = new Cases(newCaseNumber);

            cases.getAccounts().add(currentUser);

            casesRepository.save(cases);
        } else {
            String sanitized = caseNumberSanitation(report.caseNumber());
            cases = casesRepository.findFirstByCaseNumber(sanitized)
                    .orElseThrow(() -> new RuntimeException("Case not found: " + sanitized));
        }

        Report newReport = new Report(UUID.randomUUID(), report.name(), report.event(), cases);
        reportRepository.save(newReport);
    }

    private String getNextcaseNumber() {
        // handle years
        Optional<Cases> lastCase = casesRepository.findTopByOrderByCaseNumberDesc();
        if (lastCase.isPresent()) {
            String startingWith = lastCase.get().getCaseNumber().substring(0, 7);
            String serialNumber = lastCase.get().getCaseNumber().substring(8);
            int serial = Integer.parseInt(serialNumber) + 1;
            return String.format("%s%06d", startingWith, serial);
        }
        return "K-" + Year.now().getValue() + "-000001";
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
}