package org.example.crimearchive.controllers;

import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CaseAssigningController {
    private final UserService userService;
    private final CaseService caseService;

    public CaseAssigningController(UserService userService, CaseService caseService) {
        this.userService = userService;
        this.caseService = caseService;
    }


    @GetMapping("/cases")
    public String casesPage(@AuthenticationPrincipal Account user,
                            Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("allAccounts", userService.getAllAccounts());
        model.addAttribute("allUnsignedCases", caseService.getAllUnSignedCases());
        return "cases";
    }

    @PostMapping("/cases/add")
    public String casesPage(@RequestParam Long addAccountId,
                            @RequestParam String case_number,
                            @AuthenticationPrincipal Account user) {
        caseService.addAccountToCase(addAccountId, case_number);
        return "redirect:/cases";
    }


}
