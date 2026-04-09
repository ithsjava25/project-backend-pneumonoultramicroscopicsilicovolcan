package org.example.crimearchive.service;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.KNumberService;
import org.example.crimearchive.bevis.Cases;
import org.example.crimearchive.bevis.Report;
import org.example.crimearchive.mapper.ReportMapper;
import org.example.crimearchive.permissions.PermissionRepository;
import org.example.crimearchive.repository.SimpleRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
//            String latestCaseNumber = knumberSErvice.getCaseNumber();
//            permissionRepository.save(new Cases(latestCaseNumber));
            Optional<Cases> casenumber = permissionRepository.findTopByOrderByCaseNumberDesc();
            if (casenumber.isEmpty()) {
                permissionRepository.save(new Cases("K-2026-000001"));
                simpleRepository.save(ReportMapper.toEntity(report));
            } else {
                casenumber.get().getCaseNumber().substring(8);
            }
            //simpleRepository.save(ReportMapper.toEntity(new CreateReport(report.event(), report.name(), latestCaseNumber)));
        } else {
            Optional<Cases> caseNumber = permissionRepository.findFirstByCaseNumber(report.caseNumber());
            if (caseNumber.isEmpty()) throw new RuntimeException("falty case number");
            simpleRepository.save(ReportMapper.toEntity(report));
            String santiziedNumber = caseNumberSanitation(report.caseNumber());
            //if (caseNumberExists(santiziedNumber))
            //    simpleRepository.save(ReportMapper.toEntity(report));
            //simpleRepository.save(ReportMapper.toEntity(new CreateReport(report.event(), report.name(), knumberSErvice.getCaseNumber())));
        }
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
