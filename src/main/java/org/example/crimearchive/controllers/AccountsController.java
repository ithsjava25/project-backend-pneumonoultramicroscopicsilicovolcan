package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.Polis.DTOCreatePolis;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountsController {

    UserService userService;

    public AccountsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/accounts")
    public String adminPage(@AuthenticationPrincipal Account user, Model model) {
        model.addAttribute("accountoverview", user.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("currentUser", user);
        model.addAttribute("allAccounts", userService.getAllAccounts());
        return "accountoverview";
    }

    @GetMapping("/accounts/detail")
    public String accountDetails(@RequestParam Long userId) {

        return "" + userId;
    }

    @GetMapping("/accounts/add")
    public String createNewAccount(@AuthenticationPrincipal Account user, Model model) {
        model.addAttribute("accountoverview", user.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("currentUser", user);
        model.addAttribute("createAccount", new DTOCreatePolis());
        return "newaccountpage";
    }

    @PostMapping("/accounts/add")
    public String saveNewAccount(@ModelAttribute("createAccount") @Valid DTOCreatePolis newAccount,
                                 BindingResult bindingResult,
                                 @AuthenticationPrincipal Account user,
                                 Model model) {
        model.addAttribute("accountoverview", user.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("currentUser", user);
        if (bindingResult.hasErrors()) {

            return "newaccountpage";
        }
        try {
            userService.saveNewAccount(newAccount);
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("roles", "error.createAccount", e.getMessage());
            return "newaccountpage";
        }
        return "redirect:/accounts";
    }
}
