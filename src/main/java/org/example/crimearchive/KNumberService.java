package org.example.crimearchive;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Year;

@Service
public class KNumberService {

    private final JdbcTemplate jdbcTemplate;

    public KNumberService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getKNumber() {
        Long sequenceValue = jdbcTemplate.queryForObject(
                "SELECT nextval('knummer_seq')", Long.class);

        int year = Year.now().getValue();

        return String.format("K-%d-%06d", year, sequenceValue);
    }
}
