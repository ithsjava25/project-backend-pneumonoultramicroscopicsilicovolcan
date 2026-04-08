package org.example.crimearchive.service;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.KNumberService;
import org.example.crimearchive.bevis.Report;
import org.example.crimearchive.mapper.ReportMapper;
import org.example.crimearchive.repository.SimpleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    private final SimpleRepository simpleRepository;
    private final KNumberService knumberSErvice;


    public ReportService(SimpleRepository simpleRepository, KNumberService kservice) {
        this.simpleRepository = simpleRepository;
        this.knumberSErvice = kservice;
    }

    public void saveReport(CreateReport report) {
        if (report.caseNumber() == null || report.caseNumber().isBlank()) {
            String lastCaseNumber = knumberSErvice.getCaseNumber();
            simpleRepository.save(ReportMapper.toEntity(new CreateReport(report.event(), report.name(), lastCaseNumber)));
        } else {
            simpleRepository.save(ReportMapper.toEntity(report));
        }
    }

    public List<Report> getAllReports() {
        return simpleRepository.findAll();
    }
}
