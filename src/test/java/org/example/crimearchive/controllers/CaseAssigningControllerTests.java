package org.example.crimearchive.controllers;

import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.polis.Account;
import org.junit.jupiter.api.Test;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CaseAssigningControllerTests extends IntegrationBaseTest {

    @Test
    void handlerCanAccessCases() throws Exception {
        Account myHandler = createAndSaveTestUser("handler", "handler");
        createCaseAndSave();
        mockMvc.perform(get("/cases")
                        .with(user(myHandler)))
                .andExpect(status().isOk())
                .andExpect(view().name("cases"))
                .andExpect(model().attribute("allUnsignedCases", hasSize(1)));
    }

    @Test
    void onlyHandlerCanAccessCases() throws Exception{
        Account myUser = createAndSaveTestUser("user", "user");
        Account myAdmin = createAndSaveTestUser("admin", "admin");

        mockMvc.perform(get("/cases").
                with(user(myUser)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/cases")
                .with(user(myAdmin)))
                .andExpect(status().isForbidden());
    }

    @Test
    void onlyHandlerCanAddAccountsToCase() throws Exception{
        Account myHandler = createAndSaveTestUser("handler", "handler");
        Account myUser = createAndSaveTestUser("user", "user");
        Cases testCase = createCaseAndSave();
        String kNumber = testCase.getCaseNumber();

        mockMvc.perform(post("/cases/add")
                .with(user(myHandler))
                        .with(csrf())
                .param("addAccountId", myUser.getId().toString())
                .param("case_number", kNumber))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/cases/add")
                .with(user(myUser))
                        .with(csrf())
                .param("addAccountId", myUser.getId().toString())
                .param("case_number", kNumber))
                .andExpect(status().isForbidden());
    }
}
