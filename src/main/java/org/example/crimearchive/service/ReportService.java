package org.example.crimearchive.service;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.KNumberService;
import org.example.crimearchive.bevis.Cases;
import org.example.crimearchive.bevis.Report;
import org.example.crimearchive.permissions.PermissionRepository;
import org.example.crimearchive.repository.SimpleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private final SimpleRepository simpleRepository;
    private final KNumberService knumberSErvice;
    private final PermissionRepository permissionRepository;


    public ReportService(SimpleRepository simpleRepository, KNumberService kservice, PermissionRepository permissionRepository) {
        this.simpleRepository = simpleRepository;
        this.knumberSErvice = kservice;
        this.permissionRepository = permissionRepository;
    }

    public void saveReport(CreateReport report) {
        if (report.caseNumber() == null || report.caseNumber().isBlank()) {
            Cases newCase = new Cases(getNextcaseNumber());
            permissionRepository.save(newCase);
            simpleRepository.save(newCase.addReport(report));
        } else {
            if (permissionRepository.existsByCaseNumber(report.caseNumber())) {
                Optional<Cases> oldCase = permissionRepository.findFirstByCaseNumber(report.caseNumber());
                simpleRepository.save(oldCase.get().addReport(report));
            } else {
                throw new RuntimeException("Wrong case number");
            }
        }
    }

    private String getNextcaseNumber() {
        // handle years
        Optional<Cases> lastCase = permissionRepository.findTopByOrderByCaseNumberDesc();
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

//    public boolean caseNumberExists(String caseNumber) {
//        return simpleRepository.existsByCaseNumber(caseNumber);
//    }

    @PreAuthorize("(T(org.example.crimearchive.permissions.DocumentPermissionEvaluator))")
    public List<Report> getAllReports() {
        return simpleRepository.findAll();
    }

    public long getAmount() {
        return simpleRepository.count();
    }
}
