package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.bevis.Report;
import org.example.crimearchive.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }
    @GetMapping("/reports/{uuid}/download/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID uuid) throws IOException {
        return reportService.downloadPdf(uuid);
    }

    @GetMapping("/reports/{uuid}/download/file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID uuid) throws IOException {
        return reportService.downloadFile(uuid);
    }
}