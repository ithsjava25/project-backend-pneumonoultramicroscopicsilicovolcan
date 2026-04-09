package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.permissions.PermissionRepository;
import org.example.crimearchive.permissions.PermissionService;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.service.ReportService;
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

@Controller
public class HomeController {

    Logger log = LoggerFactory.getLogger(HomeController.class);
    private final ReportService reportService;
    private final PermissionService permissionService;
    private final PermissionRepository permissionRepository;

    public HomeController(ReportService reportService, PermissionService permissionService, PermissionRepository permissionRepository) {
        this.reportService = reportService;
        this.permissionService = permissionService;
        this.permissionRepository = permissionRepository;
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
            model.addAttribute("cases", permissionRepository.findByAccountsId(accountId));
            model.addAttribute("accountId", accountId);
        }
        return "cases";
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
}