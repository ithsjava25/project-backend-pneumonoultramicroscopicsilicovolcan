package org.example.crimearchive.repository;

import org.example.crimearchive.bevis.Report;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReportInitilizer implements CommandLineRunner {
    private final SimpleRepository simpleRepository;

    public ReportInitilizer(SimpleRepository simpleRepository) {
        this.simpleRepository = simpleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (simpleRepository.count() == 0) {
            simpleRepository.save(new Report(UUID.randomUUID(), "Fortkörning", "Pelle Svanslös", "K-2026-000001"));
            simpleRepository.save(new Report(UUID.randomUUID(), "Misshandel", "Seth Rydell", "K-2026-000002"));
            simpleRepository.save(new Report(UUID.randomUUID(), "Pengatvätt", "Seth Rydell", "K-2026-000002"));
        }
    }
}
