package org.example.crimearchive.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConfig {

    @Bean
    public ApplicationRunner createSequence(JdbcTemplate jdbcTemplate) {
        return args -> {
            jdbcTemplate.execute(
                    "CREATE SEQUENCE IF NOT EXISTS knummer_seq START 1 INCREMENT 1"
            );
        };
    }
}