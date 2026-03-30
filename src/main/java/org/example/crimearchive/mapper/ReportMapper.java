package org.example.crimearchive.mapper;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.bevis.Report;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReportMapper {

    public static Report toEntity(CreateReport report) {
        Report entity = new Report();
        entity.setUuid(UUID.randomUUID());
        entity.setName(report.name());
        entity.setFirstName(report.firstName());
        entity.setLastName(report.lastName());
        entity.setPhoneNumber(report.phoneNumber());
        entity.setEmail(report.email());
        entity.setEvent(report.event());
        entity.setLocation(report.location());
        entity.setDescription(report.description());
        entity.setCreatedAt(LocalDateTime.now());

        entity.setPoliceName(report.policeName());
        entity.setLawyerName(report.lawyerName());
        entity.setProsecutorName(report.prosecutorName());

        entity.setPoliceAuthority("VÄSTRA GÖTALANDS LÄN");
        entity.setCaseNumber("012-12023");
        entity.setUnit("9B3");
        entity.setAuthorityCode("0942");

        return entity;
    }
}
