package org.example.crimearchive.mapper;

import org.example.crimearchive.dto.CreateReport;
import org.example.crimearchive.reports.Report;

import java.util.UUID;

public class ReportMapper {

    public static Report toEntity(CreateReport report, String s3KeyPdf, String s3KeyFile) {
        return new Report(
                UUID.randomUUID(),
                report.name(),
                report.event(),
                s3KeyPdf,
                s3KeyFile

        );
    }

}
