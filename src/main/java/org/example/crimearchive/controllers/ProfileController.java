package org.example.crimearchive.controllers;

import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.polis.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProfileController {

    private final CaseService caseService;

    public ProfileController(CaseService caseService) {
        this.caseService = caseService;
    }


    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal Account user, Model model) {
        List<Cases> caseList = caseService.getAuthzCases(user.getId());
        model.addAttribute("assigncasebutton", user.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority()
                        .equals("ROLE_HANDLER")));
        model.addAttribute("currentUser", user);
        model.addAttribute("cases", caseService.getReportsWithCaseNumber(caseList));
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/caseoverview")
    @PreAuthorize("@caseSecurity.canAccessCase(#casenumber, principal)")
    public String caseoverview(@AuthenticationPrincipal Account user,
                               Model model, @RequestParam String casenumber) {

        model.addAttribute("assigncasebutton", user.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority()
                        .equals("ROLE_HANDLER")));
        model.addAttribute("assignedPolice", caseService.getAllPoliceForCase(casenumber));
        model.addAttribute("currentUser", user);
        model.addAttribute("rawcasenumber", casenumber);
        model.addAttribute("reportList", caseService.getReportSet(casenumber));
        return "caseoverview";
    }
}
