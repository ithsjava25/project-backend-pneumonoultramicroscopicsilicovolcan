package org.example.crimearchive.controllers;

import org.example.crimearchive.cases.CaseLifecycleService;
import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.cases.CaseStatus;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.polis.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ProfileController {

    private final CaseService caseService;
    private final CaseLifecycleService lifecycleService;

    public ProfileController(CaseService caseService, CaseLifecycleService lifecycleService) {
        this.caseService = caseService;
        this.lifecycleService = lifecycleService;
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

        boolean isHandler = user.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals("ROLE_HANDLER"));

        model.addAttribute("assigncasebutton", isHandler);
        model.addAttribute("isHandler", isHandler);
        model.addAttribute("assignedPolice", caseService.getAllPoliceForCase(casenumber));
        model.addAttribute("currentUser", user);
        model.addAttribute("rawcasenumber", casenumber);
        model.addAttribute("reportList", caseService.getReportSet(casenumber));
        model.addAttribute("caseStatus", lifecycleService.getStatus(casenumber));
        model.addAttribute("allStatuses", CaseStatus.values());
        model.addAttribute("events", lifecycleService.getEvents(casenumber));
        model.addAttribute("comments", lifecycleService.getComments(casenumber));
        return "caseoverview";
    }

    @PostMapping("/caseoverview/status")
    @PreAuthorize("@caseSecurity.canAccessCase(#casenumber, principal)")
    public String changeStatus(@RequestParam String casenumber,
                               @RequestParam CaseStatus status,
                               @AuthenticationPrincipal Account user) {
        lifecycleService.changeStatus(casenumber, status, user.getUsername());
        return "redirect:/caseoverview?casenumber=" + casenumber;
    }

    @PostMapping("/caseoverview/comment")
    @PreAuthorize("@caseSecurity.canAccessCase(#casenumber, principal)")
    public String addComment(@RequestParam String casenumber,
                             @RequestParam String content,
                             @AuthenticationPrincipal Account user) {
        if (content != null && !content.isBlank()) {
            lifecycleService.addComment(casenumber, content, user.getUsername());
        }
        return "redirect:/caseoverview?casenumber=" + casenumber;
    }
}
