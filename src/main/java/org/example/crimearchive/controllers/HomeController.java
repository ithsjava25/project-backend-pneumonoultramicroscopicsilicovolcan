package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.cases.CasesRepository;
import org.example.crimearchive.evidence.EvidenceFile;
import org.example.crimearchive.evidence.EvidenceFileService;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.reports.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    Logger log = LoggerFactory.getLogger(HomeController.class);
    private final ReportService reportService;
    private final CaseService caseService;
    private final CasesRepository casesRepository;
    private final EvidenceFileService evidenceFileService;

    public HomeController(ReportService reportService, CaseService caseService,
                          CasesRepository casesRepository, EvidenceFileService evidenceFileService) {
        this.reportService = reportService;
        this.caseService = caseService;
        this.casesRepository = casesRepository;
        this.evidenceFileService = evidenceFileService;
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
            List<Cases> cases = casesRepository.findByAccountsId(accountId);
            model.addAttribute("cases", cases);
            model.addAttribute("accountId", accountId);

            Map<String, List<EvidenceFile>> evidenceMap = new HashMap<>();
            for (Cases c : cases) {
                evidenceMap.put(c.getCaseNumber(), evidenceFileService.getByCaseNumber(c.getCaseNumber()));
            }
            model.addAttribute("evidenceMap", evidenceMap);
        }
        return "cases";
    }

    @PostMapping("/cases/add")
    public String casesPage(@RequestParam Long addAccountId,
                            @RequestParam String case_number) {
        caseService.addAccountToCase(addAccountId, case_number);
        return "redirect:/cases";
    }

    @PostMapping("/reports/add")
    public String saveReport(
            @ModelAttribute("newReport") @Valid CreateReport newReport,
            BindingResult bindingResult,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Account currentUser) throws IOException {

        if (bindingResult.hasErrors()) {
            log.info("Binding Error: {}", bindingResult.getAllErrors());
            return "private";
        }

        String caseNumber = reportService.saveReport(newReport, currentUser);

        if (file != null && !file.isEmpty()) {
            evidenceFileService.upload(caseNumber, newReport.name(), newReport.event(), file, currentUser.getUsername());
        }

        return "redirect:/userpage";
    }
}
