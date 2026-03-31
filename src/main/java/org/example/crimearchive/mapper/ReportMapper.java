package org.example.crimearchive.mapper;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.bevis.Report;

import java.util.UUID;

public class ReportMapper {

    public static Report toEntity(CreateReport report, String s3Key) {
        return new Report(
                UUID.randomUUID(),
                report.name(),
                report.event(),
                s3Key

        );
    }

}
