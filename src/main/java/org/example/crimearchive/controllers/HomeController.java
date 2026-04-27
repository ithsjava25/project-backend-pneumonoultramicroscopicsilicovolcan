package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.cases.CasesRepository;
import org.example.crimearchive.evidence.EvidenceFileService;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.reports.ReportService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Controller
public class HomeController {

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
        return "redirect:/profile";
    }

    @GetMapping("/reports")
    public String privatePage(@AuthenticationPrincipal Account currentUser, Model model) {

        CreateReport report = new CreateReport("", "", "", "", "");

        model.addAttribute("newReport", report);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("reports", reportService.getAllReports());

        model.addAttribute("assigncasebutton",
                currentUser.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_HANDLER")));

        model.addAttribute("accountoverview",
                currentUser.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        return "reports";
    }

    @GetMapping("/userpage")
    public String userPage(@AuthenticationPrincipal Account user, Model model) {
        model.addAttribute("reportAmount", reportService.getAmount());
        model.addAttribute("reports", reportService.getAllReports());
        return "userpage";
    }

    @PostMapping("/reports")
    public String saveReport(
            @ModelAttribute("newReport") @Valid CreateReport newReport,
            BindingResult bindingResult,
            @RequestParam(required = false) List<MultipartFile> files,
            @RequestParam(required = false) List<MultipartFile> images,
            @AuthenticationPrincipal Account currentUser,
            Model model) {

        if (bindingResult.hasErrors()) {
            populateReportsModel(model, currentUser);
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
            populateReportsModel(model, currentUser);
            model.addAttribute("errorMessage", "Något gick fel vid sparande");
            return "reports";
        }

        return "redirect:/reports";
    }

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("title", "Åtkomst nekad");
        model.addAttribute("message", "Du saknar rättigheter för att visa denna sida.");
        return "error/error";
    }

    private void populateReportsModel(Model model, Account currentUser) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("reports", reportService.getAllReports());

        model.addAttribute("assigncasebutton",
                currentUser.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_HANDLER")));

        model.addAttribute("accountoverview",
                currentUser.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }
}
