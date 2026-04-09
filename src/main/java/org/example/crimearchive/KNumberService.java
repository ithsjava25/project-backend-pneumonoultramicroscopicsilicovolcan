package org.example.crimearchive;

import org.example.crimearchive.reports.ReportRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class KNumberService {

    private final JdbcTemplate jdbcTemplate;
    private final ReportRepository repository;

    public KNumberService(JdbcTemplate jdbcTemplate, ReportRepository repository) {
        this.jdbcTemplate = jdbcTemplate;
        this.repository = repository;
    }

    public String getKNumber() {
        Long sequenceValue = jdbcTemplate.queryForObject(
                "SELECT nextval('knummer_seq')", Long.class);

        int year = Year.now().getValue();

        return String.format("K-%d-%06d", year, sequenceValue);
    }

//    public String getCaseNumber() {
//        int year = Year.now().getValue();
//        String knumber = repository.findLastKnumber(String.valueOf(year));
//        if (knumber == null) {
//            return String.format("K-%d-%06d", year, 1);
//        } else {
//            int intNumber = Integer.parseInt(knumber.substring(7));
//            return String.format("K-%d-%06d", year, (intNumber + 1));
//        }
//    }
}
