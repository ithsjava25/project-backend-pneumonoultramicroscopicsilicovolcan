package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.dto.CreateReport;
import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.cases.CasesRepository;
import org.example.crimearchive.evidence.EvidenceFileService;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.reports.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class HomeController {

    Logger log = LoggerFactory.getLogger(HomeController.class);
    private final ReportService reportService;
    private final EvidenceFileService evidenceFileService;
    private final CaseService caseService;
    private final CasesRepository casesRepository;

    public HomeController(ReportService reportService, EvidenceFileService evidenceFileService,
                          CaseService caseService, CasesRepository casesRepository) {
        this.reportService = reportService;
        this.evidenceFileService = evidenceFileService;
        this.caseService = caseService;
        this.casesRepository = casesRepository;
    }

    @GetMapping("/")
    public String indexPage(Model model) {
        return "index";
    }

    @GetMapping("/reports")
    public String privatePage(@AuthenticationPrincipal Account user, Model model) {
        model.addAttribute("newReport", new CreateReport());
        return "registerreport";
    }

    @GetMapping("/userpage")
    public String userPage(@AuthenticationPrincipal Account user, Model model) {
        model.addAttribute("reportAmount", reportService.getAmount());
        return "userpage";
    }

    @PostMapping("/reports/add")
    public String saveReport(
            @ModelAttribute("newReport") @Valid CreateReport newReport,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile file,
            @AuthenticationPrincipal Account currentUser) {

        if (bindingResult.hasErrors()) {
            log.info("Binding Error: {}", bindingResult.getAllErrors().getLast());
            return "registerreport";
        }

        try {
            String caseNumber = reportService.saveReport(newReport, currentUser);
            evidenceFileService.upload(caseNumber, newReport.name(), newReport.event(),
                    file, currentUser.getUsername());
        } catch (Exception e) {
            log.error("Fel vid sparande av rapport", e);
            return "registerreport";
        }

        return "redirect:/userpage";
    }

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("title", "Åtkomst nekad");
        model.addAttribute("message", "Du saknar rättigheter för att visa denna sida.");
        return "error/error";
    }
}
