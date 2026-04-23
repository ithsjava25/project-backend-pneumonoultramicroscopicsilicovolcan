package org.example.crimearchive.controllers;

import org.example.crimearchive.KNumberService;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.cases.CasesRepository;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public abstract class IntegrationBaseTest {

    @ServiceConnection
    protected static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected CasesRepository casesRepository;
    @Autowired
    protected KNumberService Kservice;
    @Autowired
    protected PasswordEncoder encoder;
    @MockitoBean
    protected S3Client minio;


    protected Account createAndSaveTestUser(String username, String role) {
        Account user = new Account();
        user.setUsername(username);
        user.setPassword(encoder.encode("password"));
        user.setAuthorities(List.of(role));
        user.setFullName("Test gubbe");
        user.setProfession("PJ");
        user.setDepartment("Home");
        return userRepository.save(user);
    }

    protected Cases createCaseAndSave(){
        Cases newCase = new Cases();
        newCase.setCaseNumber(Kservice.getKNumber());
        return casesRepository.save(newCase);
    }
}
