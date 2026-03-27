package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

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
    public String privatePage(Model model) {
        model.addAttribute("newReport", new CreateReport());
        return "private";
    }

    @PostMapping("/reports/add")
    public String saveReport(@ModelAttribute("newReport") @Valid CreateReport newReport) {
        reportService.saveReport(newReport);
        return "redirect:/";
    }
}
