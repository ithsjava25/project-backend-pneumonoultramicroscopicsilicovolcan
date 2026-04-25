package org.example.crimearchive.controllers;

import org.example.crimearchive.dto.Polis.DTOCreatePolis;
import org.example.crimearchive.dto.Polis.DTOUpdatePolis;
import org.example.crimearchive.polis.Account;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountControllerTests extends IntegrationBaseTest {


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
    void adminCannotElevatePrivilegesOnSelf() throws Exception {
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

    @Test
    @Transactional
    void adminCanSaveNewAccounts() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");

        mockMvc.perform(post("/accounts/add")
                        .with(user(myAdmin))
                        .with(csrf())
                        .param("fullName", "name")
                        .param("profession", "pro")
                        .param("department", "dep")
                        .param("username", "username")
                        .param("password", "password")
                        .param("roles", "user"))
                .andExpect(status().is3xxRedirection());

        Account savedAcc = userRepository.findUserByUsername("username");
        assertEquals("name", savedAcc.getFullName());
    }

    @Test
    void nonAdminCannotPostNewAccount() throws Exception {
        Account myUser = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/accounts/add")
                        .with(user(myUser))
                        .with(csrf())
                        .param("fullName", "name")
                        .param("profession", "pro")
                        .param("department", "dep")
                        .param("username", "username")
                        .param("password", "password")
                        .param("roles", "user"))
                .andExpect(status().isForbidden());

    }

    @Test
    void updateAccountWithInvalidInputdontRedirect() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");
        Account updateAcc = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/accounts/detail")
                        .with(user(myAdmin))
                        .with(csrf())
                        .param("id", updateAcc.getId().toString())
                        .param("fullName", "")
                        .param("profession", updateAcc.getProfession())
                        .param("department", updateAcc.getDepartment())
                        .param("username", updateAcc.getUsername())
                        .param("roles", "user"))
                .andExpect(status().isOk())
                .andExpect(view().name("updateaccountpage"));
    }


    @Test
    void updateAccountWithNonExistantRolesDontRedirect() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");
        Account updateAcc = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/accounts/detail")
                        .with(user(myAdmin))
                        .with(csrf())
                        .param("id", updateAcc.getId().toString())
                        .param("fullName", "newname")
                        .param("profession", updateAcc.getProfession())
                        .param("department", updateAcc.getDepartment())
                        .param("username", updateAcc.getUsername())
                        .param("roles", "noSuchRole"))
                .andExpect(status().isOk())
                .andExpect(view().name("updateaccountpage"));
    }

    @Test
    void updateAccountWithInvalidPasswordDontRedirect() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");
        Account updateAcc = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/accounts/detail")
                        .with(user(myAdmin))
                        .with(csrf())
                        .param("id", updateAcc.getId().toString())
                        .param("fullName", "newname")
                        .param("profession", updateAcc.getProfession())
                        .param("department", updateAcc.getDepartment())
                        .param("username", updateAcc.getUsername())
                        .param("password", "eror")
                        .param("roles", "user"))
                .andExpect(status().isOk())
                .andExpect(view().name("updateaccountpage"));
    }
    @Test
    void newAccountWithInvalidInputdontRedirect() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");
        Account updateAcc = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/accounts/add")
                        .with(user(myAdmin))
                        .with(csrf())
                        .param("id", updateAcc.getId().toString())
                        .param("fullName", "")
                        .param("profession", updateAcc.getProfession())
                        .param("department", updateAcc.getDepartment())
                        .param("username", updateAcc.getUsername())
                        .param("roles", "user"))
                .andExpect(status().isOk())
                .andExpect(view().name("newaccountpage"));
    }


    @Test
    void newAccountWithNonExistantRolesDontRedirect() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");
        Account updateAcc = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/accounts/add")
                        .with(user(myAdmin))
                        .with(csrf())
                        .param("id", updateAcc.getId().toString())
                        .param("fullName", "newname")
                        .param("profession", updateAcc.getProfession())
                        .param("department", updateAcc.getDepartment())
                        .param("username", updateAcc.getUsername())
                        .param("roles", "noSuchRole"))
                .andExpect(status().isOk())
                .andExpect(view().name("newaccountpage"));
    }

    @Test
    void newAccountWithInvalidPasswordDontRedirect() throws Exception {
        Account myAdmin = createAndSaveTestUser("admin", "admin");
        Account updateAcc = createAndSaveTestUser("user", "user");

        mockMvc.perform(post("/accounts/add")
                        .with(user(myAdmin))
                        .with(csrf())
                        .param("id", updateAcc.getId().toString())
                        .param("fullName", "newname")
                        .param("profession", updateAcc.getProfession())
                        .param("department", updateAcc.getDepartment())
                        .param("username", updateAcc.getUsername())
                        .param("password", "eror")
                        .param("roles", "user"))
                .andExpect(status().isOk())
                .andExpect(view().name("newaccountpage"));
    }
}
