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
            @RequestParam(required = false) java.util.List<MultipartFile> files,
            @RequestParam(required = false) java.util.List<MultipartFile> images,
            @AuthenticationPrincipal Account currentUser) {

        log.info("saveReport() anropad");

        if (bindingResult.hasErrors()) {
            log.info("Binding Error: {}", bindingResult.getAllErrors().getLast());
            return "reports";
        }

        try {
            log.info("Sparar rapport för user: {}", currentUser != null ? currentUser.getUsername() : "NULL");

            String caseNumber = reportService.saveReport(newReport, currentUser);
            log.info("Rapport sparad med caseNumber: {}", caseNumber);

            // 🔹 Filer
            if (files != null) {
                for (MultipartFile f : files) {
                    log.info("Fil mottagen: {}", f.getOriginalFilename());

                    if (!f.isEmpty()) {
                        log.info("Laddar upp fil: {}", f.getOriginalFilename());

                        evidenceFileService.upload(
                                caseNumber,
                                newReport.name(),
                                newReport.event(),
                                f,
                                currentUser.getUsername()
                        );
                    } else {
                        log.warn("Fil var tom: {}", f.getOriginalFilename());
                    }
                }
            } else {
                log.info("Inga filer skickades");
            }

            // 🔹 Bilder
            if (images != null) {
                for (MultipartFile img : images) {
                    log.info("Bild mottagen: {}", img.getOriginalFilename());

                    if (!img.isEmpty()) {
                        log.info("Laddar upp bild: {}", img.getOriginalFilename());

                        evidenceFileService.upload(
                                caseNumber,
                                newReport.name(),
                                newReport.event(),
                                img,
                                currentUser.getUsername()
                        );
                    } else {
                        log.warn("Bild var tom: {}", img.getOriginalFilename());
                    }
                }
            } else {
                log.info("Inga bilder skickades");
            }

        } catch (Exception e) {
            log.error("Fel vid sparande av rapport", e);
            return "reports";
        }

        log.info("Redirect till /reports");
        return "redirect:/reports";
    }

    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("title", "Åtkomst nekad");
        model.addAttribute("message", "Du saknar rättigheter för att visa denna sida.");
        return "error/error";
    }
}
