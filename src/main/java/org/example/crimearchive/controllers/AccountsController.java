package org.example.crimearchive.controllers;

import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AccountsController {

    UserService userService;

    public AccountsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/accounts")
    public String adminPage(@AuthenticationPrincipal Account user, Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("allAccounts", userService.getAllAccounts());

        return "accountoverview";
    }

    @GetMapping("/accounts/detail")
    public ResponseEntity accountDetails(@RequestParam Long userId) {

        return ResponseEntity.ok("user id: " + userId);
    }
}
