package org.example.crimearchive.controllers;

import org.example.crimearchive.polis.Account;
import org.junit.jupiter.api.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CaseAssigningControllerTests extends IntegrationBaseTest {

    @Test
    void handlerCanAccessCases() throws Exception {
        Account myHandler = createAndSaveTestUser("handler", "handler");

        mockMvc.perform(get("/cases")
                        .with(user(myHandler)))
                .andExpect(status().isOk());
    }
}
