package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.CreateReport;
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

@Controller
public class HomeController {

    Logger log = LoggerFactory.getLogger(HomeController.class);
    ReportService reportService;

    public HomeController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/")
    public String indexPage(Model model) {
        //model.addAttribute("reports", reportService.getAllReports());
        return "index";
    }

    @GetMapping("/private")
    public String privatePage(@AuthenticationPrincipal Account user, Model model) {
        model.addAttribute("newReport", new CreateReport());
        return "private";
    }

    @GetMapping("/userpage")
    public String userPage(Model model){
        return "userpage";
    }

    @PostMapping("/reports/add")
    public String saveReport(@ModelAttribute("newReport") @Valid CreateReport newReport, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("Binding Error: {}", bindingResult.getAllErrors().getLast());
            return "private";
        }
        reportService.saveReport(newReport);
        return "redirect:/";
    }
}
