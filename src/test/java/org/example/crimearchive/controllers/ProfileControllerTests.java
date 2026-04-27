package org.example.crimearchive.controllers;

import org.example.crimearchive.DTO.Polis.DTOUpdateProfile;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.polis.Account;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProfileControllerTests extends IntegrationBaseTest{

    @Test
    void onlyLoggedinWithAtLeastUserCanAccess() throws Exception{
        Account myUser = createAndSaveTestUser("user","user");

        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/profile")
                .with(user(myUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"));
    }

    @Test
    void userUpdateWithValidInputRedirects() throws Exception{
        Account myUser = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/profile/update")
                .with(user(myUser))
                .with(csrf())
                .param("fullname", "testsson")
                .param("password", "newpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        Account updatedUser = userRepository.findById(myUser.getId()).get();
                assertEquals("testsson", updatedUser.getFullName());
    }

    @Test
    void userUpdateWithInvalidNameReturnsPage() throws Exception{
        Account myUser = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/profile/update")
                .with(user(myUser))
                .with(csrf())
                .param("fullname", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("updateProfile", isA(DTOUpdateProfile.class)));

    }

    @Test
    void userUpdateWithInvalidPasswordReturnsPage() throws Exception{
        Account myUser = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/profile/update")
                .with(user(myUser))
                .with(csrf())
                        .param("fullname", "Testsson")
                .param("password", "eror"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("updateProfile", isA(DTOUpdateProfile.class)));
    }

    @Test
    void userWithCaseAccessCanViewCaseoverview() throws Exception{
        Account caseAccess = createAndSaveTestUser("user","user");
        Cases testCase = createCaseAndSave();
        testCase.addAccountToCase(caseAccess);

        mockMvc.perform(get("/caseoverview")
                .with(user(caseAccess))
                .param("casenumber", testCase.getCaseNumber()))
                .andExpect(status().isOk())
                .andExpect(view().name("caseoverview"));
    }

    @Test
    void userWithoutCaseAccessCanViewCaseoverviewButRedacted() throws Exception {
        Account caseAccess = createAndSaveTestUser("user","user");
        Cases testCase = createCaseAndSave();

        mockMvc.perform(get("/caseoverview")
                .with(user(caseAccess))
                .param("casenumber", testCase.getCaseNumber()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("canAdd", false));
    }
}
