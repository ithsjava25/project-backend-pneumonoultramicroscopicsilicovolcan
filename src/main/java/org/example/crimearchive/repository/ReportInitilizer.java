package org.example.crimearchive.repository;

import org.example.crimearchive.dto.CreateReport;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.example.crimearchive.reports.ReportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ReportInitilizer implements CommandLineRunner {

    private final ReportService reportService;
    private final UserRepository userRepository;

    public ReportInitilizer(ReportService reportService, UserRepository userRepository) {
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (reportService.getAmount() == 0) {
            Account admin = userRepository.findUserByUsername("admin");
            if (admin == null) {
                return;
            }
            reportService.saveReport(new CreateReport("Fortkörning", "Pelle Svanslös", ""), admin);
            reportService.saveReport(new CreateReport("Misshandel", "Seth Rydell", ""), admin);
            reportService.saveReport(new CreateReport("Pengatvätt", "Seth Rydell", ""), admin);
        }
    }
}
