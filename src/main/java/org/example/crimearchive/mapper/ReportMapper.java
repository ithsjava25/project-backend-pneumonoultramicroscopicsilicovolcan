package org.example.crimearchive.mapper;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.bevis.Report;

import java.util.UUID;

public class ReportMapper {

    public static Report toEntity(CreateReport report) {
        return new Report(
                UUID.randomUUID(),
                report.name(),
                report.event()
        );
    }

}
