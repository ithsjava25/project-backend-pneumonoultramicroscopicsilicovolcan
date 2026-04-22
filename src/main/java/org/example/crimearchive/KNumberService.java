package org.example.crimearchive;

import jakarta.annotation.PostConstruct;
import org.example.crimearchive.reports.ReportRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class KNumberService {

    private final JdbcTemplate jdbcTemplate;

    public KNumberService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @PostConstruct
    public void init() {
        jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS knummer_seq START 1 INCREMENT 1");
    }

    public String getKNumber() {
        Long sequenceValue = jdbcTemplate.queryForObject(
                "SELECT nextval('knummer_seq')", Long.class);

        int year = Year.now().getValue();

        return String.format("K-%d-%06d", year, sequenceValue);
    }
}
