package org.example.crimearchive.repository;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.example.crimearchive.service.ReportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ReportInitilizer implements CommandLineRunner {

    private final SimpleRepository simpleRepository;
    private final ReportService reportService;
    private final UserRepository userRepository;

    public ReportInitilizer(SimpleRepository simpleRepository, ReportService reportService, UserRepository userRepository) {
        this.simpleRepository = simpleRepository;
        this.reportService = reportService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (simpleRepository.count() == 0) {
            Account admin = userRepository.findUserByUsername("admin");
            reportService.saveReport(new CreateReport("Fortkörning", "Pelle Svanslös"), admin);
            reportService.saveReport(new CreateReport("Misshandel", "Seth Rydell"), admin);
            reportService.saveReport(new CreateReport("Pengatvätt", "Seth Rydell"), admin);
        }
    }
}