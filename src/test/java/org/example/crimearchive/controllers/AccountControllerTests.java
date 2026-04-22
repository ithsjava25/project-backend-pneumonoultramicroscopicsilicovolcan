package org.example.crimearchive.controllers;

import org.example.crimearchive.polis.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @Autowired
    UserService userService;

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


}
