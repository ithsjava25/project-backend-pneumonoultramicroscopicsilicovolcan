package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class HomeController {

    private final ReportService reportService;

    public HomeController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/")
    public String indexPage(Model model) {
        return "index";
    }

    @GetMapping("/private")
    public String privatePage(Model model) {
        model.addAttribute("newReport", new CreateReport());
        return "private";
    }

    @PostMapping("/reports/add")
    public String saveReport(
            @ModelAttribute("newReport") @Valid CreateReport newReport,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        reportService.saveReport(newReport, file);
        return "redirect:/";
    }
}