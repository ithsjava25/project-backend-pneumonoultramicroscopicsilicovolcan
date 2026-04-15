package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.cases.CasesRepository;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.reports.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    Logger log = LoggerFactory.getLogger(HomeController.class);
    private final ReportService reportService;
    private final CaseService caseService;
    private final CasesRepository casesRepository;

    public HomeController(ReportService reportService, CaseService caseService, CasesRepository casesRepository) {
        this.reportService = reportService;
        this.caseService = caseService;
        this.casesRepository = casesRepository;
    }

    @GetMapping("/")
    public String indexPage(Model model) {
        return "index";
    }

    @GetMapping("/private")
    public String privatePage(@AuthenticationPrincipal Account user, Model model) {
        model.addAttribute("newReport", new CreateReport());
        return "private";
    }

    @GetMapping("/userpage")
    public String userPage(@AuthenticationPrincipal Account user, Model model) {
        model.addAttribute("reportAmount", reportService.getAmount());
        return "userpage";
    }

    @GetMapping("/cases")
    public String casesPage(@RequestParam(required = false) Long accountId, Model model) {
        if (accountId != null) {
            model.addAttribute("cases", casesRepository.findByAccountsId(accountId));
            model.addAttribute("accountId", accountId);
        }
        return "cases";
    }

    @PostMapping("/cases/add")
    public String casesPage(@RequestParam Long addAccountId,
                            @RequestParam String case_number,
                            @AuthenticationPrincipal Account user) {
        caseService.addAccountToCase(addAccountId, case_number);
        return "redirect:/cases";
    }

    @PostMapping("/reports/add")
    public String saveReport(
            @ModelAttribute("newReport") @Valid CreateReport newReport,
            BindingResult bindingResult,
            @AuthenticationPrincipal Account currentUser) {

        if (bindingResult.hasErrors()) {
            log.info("Binding Error: {}", bindingResult.getAllErrors().getLast());
            return "private";
        }
        reportService.saveReport(newReport, currentUser);
        return "redirect:/userpage";
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal Account user, Model model) {
        List<Cases> caseList = caseService.getAuthzCases(user.getId());
        model.addAttribute("currentUser", user);
        model.addAttribute("cases", caseService.getReportsWithCaseNumber(caseList));
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/reports/{uuid}/download/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID uuid) {
        return reportService.downloadPdf(uuid);
    }

    @GetMapping("/reports/{uuid}/download/file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID uuid) {
        return reportService.downloadFile(uuid);
    }
}
