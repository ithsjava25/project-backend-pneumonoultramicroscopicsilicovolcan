package org.example.crimearchive.repository;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.example.crimearchive.reports.ReportRepository;
import org.example.crimearchive.reports.ReportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ReportInitilizer implements CommandLineRunner {

    private final ReportRepository reportRepository;
    private final ReportService reportService;
    private final UserRepository userRepository;

    public ReportInitilizer(ReportRepository reportRepository, ReportService reportService, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (reportRepository.count() == 0) {
            Account admin = userRepository.findUserByUsername("admin");
            reportService.saveReport(new CreateReport("Fortkörning", "Pelle Svanslös"), admin);
            reportService.saveReport(new CreateReport("Misshandel", "Seth Rydell"), admin);
            reportService.saveReport(new CreateReport("Pengatvätt", "Seth Rydell"), admin);
        }
    }
}