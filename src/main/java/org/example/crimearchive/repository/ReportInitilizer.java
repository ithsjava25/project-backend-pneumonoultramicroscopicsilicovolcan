package org.example.crimearchive.repository;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.service.ReportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ReportInitilizer implements CommandLineRunner {
    private final SimpleRepository simpleRepository;
    private final ReportService reportService;

    public ReportInitilizer(SimpleRepository simpleRepository, ReportService reportService) {
        this.simpleRepository = simpleRepository;
        this.reportService = reportService;
    }

    @Override
    public void run(String... args) throws Exception {
//        if (simpleRepository.count() == 0) {
//            simpleRepository.save(new Report(UUID.randomUUID(), "Fortkörning", "Pelle Svanslös", "K-2026-000001"));
//            simpleRepository.save(new Report(UUID.randomUUID(), "Misshandel", "Seth Rydell", "K-2026-000002"));
//            simpleRepository.save(new Report(UUID.randomUUID(), "Pengatvätt", "Seth Rydell", "K-2026-000002"));
//        }
        if (simpleRepository.count() == 0) {
            reportService.saveReport(new CreateReport("Fortkörning", "Pelle Svanslös"));
            reportService.saveReport(new CreateReport("Misshandel", "Seth Rydell"));
            reportService.saveReport(new CreateReport("Pengatvätt", "Seth Rydell"));
        }
    }
}
