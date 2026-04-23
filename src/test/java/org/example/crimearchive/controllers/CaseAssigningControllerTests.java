package org.example.crimearchive.controllers;

import org.example.crimearchive.polis.Account;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
//@AutoConfigureMockMvc
//@Testcontainers
public class CaseAssigningControllerTests extends IntegrationBaseTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    S3Client minio;
//
//    @Container
//    @ServiceConnection
//    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    PasswordEncoder passwordEncoder;

//    private Account createAndSaveTestUser(String username, String role) {
//        Account user = new Account();
//        user.setUsername(username);
//        user.setPassword(passwordEncoder.encode("password"));
//        user.setAuthorities(List.of(role));
//        user.setFullName("Test gubbe");
//        user.setProfession("PJ");
//        user.setDepartment("Home");
//        return userRepository.save(user);
//    }

    @Test
    void handlerCanAccessCases() throws Exception {
        Account myHandler = createAndSaveTestUser("handler", "handler");

        mockMvc.perform(get("/cases")
                        .with(user(myHandler)))
                .andExpect(status().isOk());
    }
}
