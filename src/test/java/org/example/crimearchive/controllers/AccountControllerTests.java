package org.example.crimearchive.controllers;

import org.example.crimearchive.DTO.Polis.DTOCreatePolis;
import org.example.crimearchive.DTO.Polis.DTOUpdatePolis;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    S3Client minio;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private Account createAndSaveTestUser(String username, String role) {
        Account user = new Account();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password"));
        user.setAuthorities(List.of(role));
        user.setFullName("Test gubbe");
        user.setProfession("PJ");
        user.setDepartment("Home");
        return userRepository.save(user);
    }

    /**
     * Test uses Test container with report and account initializer
     * WithUserDetails searchers the database for the username and uses that account in the test.
     *
     * @throws Exception When shit hits the fan.
     */
    @Test
    @WithUserDetails("sysadmin")
    void adminShouldSeeAccountPage() throws Exception {
        mockMvc.perform(get("/accounts"))
                //Safety
                .andExpect(status().isOk())
                //View
                .andExpect(view().name("accountoverview"))
                //Model
                .andExpect(model().attributeExists("accountoverview", "currentUser", "allAccounts"))
                .andExpect(model().attribute("accountoverview", true))
                // Correct Import here is Hamcrest isCollectionWithSize otherwise gets the objects as a list
                .andExpect(model().attribute("allAccounts", hasSize(greaterThan(0))))
                .andExpect(model().attribute("allAccounts", not(hasItem(hasProperty("username", is("sysadmin"))))));

    }

    @Test
    void nonAdminShoudNotGetAccessToAccountPage() throws Exception {
        Account nonAdmin = createAndSaveTestUser("user", "user");

        mockMvc.perform(get("/accounts")
                        .with(user(nonAdmin)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCannotElevatePrivligesOnSelf() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");

        mockMvc.perform(get("/accounts/detail")
                        .with(user(myAdmin))
                        .param("userId", myAdmin.getId().toString()))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAccountDetailsOnOthers() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");
        Account myUser = createAndSaveTestUser("user", "user");

        mockMvc.perform(get("/accounts/detail")
                        .with(user(myAdmin))
                        .with(csrf())
                        .param("userId", myUser.getId().toString()))
                .andExpect(status().isOk())

                .andExpect(view().name("updateaccountpage"))

                .andExpect(model().attribute("updateAccount", isA(DTOUpdatePolis.class)));
    }

    @Test
    @Transactional
    void adminCanChangePrivlagesOnOthers() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");
        Account myUser = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/accounts/detail")
                        .with(user(myAdmin))
                        .with(csrf())
                        .param("id", myUser.getId().toString())
                        .param("fullName", myUser.getFullName())
                        .param("profession", myUser.getProfession())
                        .param("department", myUser.getDepartment())
                        .param("username", myUser.getUsername())
                        .param("roles", "user,handler"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts"));

        Account updatedUser = userRepository.findById(myUser.getId()).get();
        assertEquals(List.of("USER", "HANDLER"), updatedUser.getAuthoritesAsStringList());
    }

    @Test
    void adminCanGoToNewAccountPage() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");

        mockMvc.perform(get("/accounts/add")
                        .with(user(myAdmin)))
                .andExpect(status().isOk())

                .andExpect(view().name("newaccountpage"))

                .andExpect(model().attribute("createAccount", isA(DTOCreatePolis.class)));
    }

    @Test
    void nonOtherThanAdminCanAccessCreateAccoutPage() throws Exception {
        Account myUser = createAndSaveTestUser("user", "user");
        Account myHandler = createAndSaveTestUser("handler", "handler");
        Account accNoRoles = createAndSaveTestUser("noroles", "");

        mockMvc.perform(get("/accounts/add")
                        .with(user(myUser)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/accounts/add")
                        .with(user(myHandler)))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/accounts/add")
                        .with(user(accNoRoles)))
                .andExpect(status().isForbidden());
    }


}
