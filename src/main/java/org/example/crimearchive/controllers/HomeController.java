package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.KNumberService;
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
    private final KNumberService kNumberService;

    public HomeController(ReportService reportService, EvidenceFileService evidenceFileService,
                          CaseService caseService, CasesRepository casesRepository,
                          KNumberService kNumberService) {
        this.reportService = reportService;
        this.evidenceFileService = evidenceFileService;
        this.caseService = caseService;
        this.casesRepository = casesRepository;
        this.kNumberService = kNumberService;
    }

    @GetMapping("/")
    public String indexPage(Model model) {
        return "index";
    }

    @GetMapping("/reports")
    public String privatePage(@AuthenticationPrincipal Account user, Model model) {

        CreateReport report = new CreateReport(
                "",
                "",
                ""
        );

        model.addAttribute("newReport", report);

        return "reports";
    }

    @GetMapping("/userpage")
    public String userPage(@AuthenticationPrincipal Account user, Model model) {
        model.addAttribute("reportAmount", reportService.getAmount());
        return "userpage";
    }

    @PostMapping("/reports")
    public String saveReport(
            @ModelAttribute("newReport") @Valid CreateReport newReport,
            BindingResult bindingResult,
            @RequestParam(required = false) java.util.List<MultipartFile> files,
            @RequestParam(required = false) java.util.List<MultipartFile> images,
            @AuthenticationPrincipal Account currentUser) {

        if (bindingResult.hasErrors()) {
            log.info("Binding Error: {}", bindingResult.getAllErrors().getLast());
            return "reports";
        }

        try {
            String caseNumber = reportService.saveReport(newReport, currentUser);

            if (files != null) {
                for (MultipartFile f : files) {
                    if (!f.isEmpty()) {
                        evidenceFileService.upload(
                                caseNumber,
                                newReport.name(),
                                newReport.event(),
                                f,
                                currentUser.getUsername()
                        );
                    }
                }
            }

            if (images != null) {
                for (MultipartFile img : images) {
                    if (!img.isEmpty()) {
                        evidenceFileService.upload(
                                caseNumber,
                                newReport.name(),
                                newReport.event(),
                                img,
                                currentUser.getUsername()
                        );
                    }
                }
            }

        } catch (Exception e) {
            log.error("Fel vid sparande av rapport", e);
            return "reports";
        }

        return "redirect:/userpage";
    }
}
