package org.example.crimearchive.reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.crimearchive.dto.CreateReport;
import org.example.crimearchive.dto.ReportResponse;
import org.example.crimearchive.KNumberService;
import org.example.crimearchive.cases.Cases;
import org.example.crimearchive.cases.CasesRepository;
import org.example.crimearchive.polis.Account;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final KNumberService knumberService;
    private final CasesRepository casesRepository;

    public ReportService(ReportRepository reportRepository, KNumberService knumberService, CasesRepository casesRepository) {
        this.reportRepository = reportReposithttps://github.com/ithsjava25/project-backend-DurTre/pull/13/conflict?name=src%252Fmain%252Fjava%252Forg%252Fexample%252Fcrimearchive%252Freports%252FReportService.java&ancestor_oid=690ead38ec7d8d1cd8a856af36a7112821febcdc&base_oid=6ea31a1a8dd80f1f190c2b39a2733e508f376539&head_oid=704f2c32be2319607cea891783e283c3d3057210ory;
        this.knumberService = knumberService;
        this.casesRepository = casesRepository;
    }

    @Transactional
    public String saveReport(CreateReport report, Account currentUser) {
        Cases cases;

        if (report.caseNumber() == null || report.caseNumber().isBlank()) {
            String newCaseNumber = knumberService.getKNumber();
            cases = new Cases(newCaseNumber);
            if (currentUser != null) cases.getAccounts().add(currentUser);
            casesRepository.save(cases);
        } else {
            String sanitized = caseNumberSanitation(report.caseNumber());
            cases = casesRepository.findFirstByCaseNumber(sanitized)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case not found: " + sanitized));
        }

        Report newReport = new Report(UUID.randomUUID(), report.name(), report.event(), cases);
        reportRepository.save(newReport);
        return cases.getCaseNumber();
    }

    @Transactional
    public void saveReportWithFile(CreateReport report, MultipartFile file) throws IOException {
        String s3KeyPdf = null;
        String s3KeyFile = null;

        try {
            ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, pdfStream);
            document.open();


            document.add(new Paragraph("Brottsanmälan"));
            document.add(new Paragraph("Namn: " + report.name()));
            document.add(new Paragraph("Brottstyp: " + report.event()));
            document.add(new Paragraph("Datum: " + LocalDateTime.now()));


            if (file != null && !file.isEmpty()) {
                document.newPage();

                if (isImage(file)) {
                    document.add(new Paragraph("Bifogat bevisfoto:"));
                    Image image = Image.getInstance(file.getBytes());
                    image.scaleToFit(500, 700);
                    document.add(image);

                } else if (isPdf(file)) {
                    document.add(new Paragraph("Bifogat dokument (PDF):"));
                    document.add(new Paragraph(file.getOriginalFilename()));
                    document.add(new Paragraph("Se separat bifogad fil för fullständigt dokument."));

                } else if (isWord(file)) {
                    document.add(new Paragraph("Bifogat dokument (Word):"));
                    document.add(new Paragraph(file.getOriginalFilename()));
                    document.add(new Paragraph("Se separat bifogad fil för fullständigt dokument."));

                } else {
                    document.add(new Paragraph("Bifogad fil: " + file.getOriginalFilename()));
                }
            }

            document.close();

            byte[] pdfBytes = pdfStream.toByteArray();
            s3KeyPdf = "reports/pdf/" + UUID.randomUUID() + ".pdf";

            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(s3KeyPdf)
                            .contentType("application/pdf")
                            .build(),
                    RequestBody.fromBytes(pdfBytes)
            );

            if (file != null && !file.isEmpty()) {
                s3KeyFile = "reports/files/" + UUID.randomUUID();

                s3Client.putObject(
                        PutObjectRequest.builder()
                                .bucket(bucket)
                                .key(s3KeyFile)
                                .contentType(file.getContentType())
                                .build(),
                        RequestBody.fromInputStream(file.getInputStream(), file.getSize())
                );
            }

            Cases cases;
            if (report.caseNumber() == null || report.caseNumber().isBlank()) {
                String newCaseNumber = knumberService.getKNumber();
                cases = new Cases(newCaseNumber);
                casesRepository.save(cases);
            } else {
                String sanitized = caseNumberSanitation(report.caseNumber());
                cases = casesRepository.findFirstByCaseNumber(sanitized)
                        .orElseThrow(() -> new RuntimeException("Case not found: " + sanitized));
            }

            Report newReport = ReportMapper.toEntity(report, s3KeyPdf, s3KeyFile);
            newReport.setCaseEntity(cases);
            reportRepository.save(newReport);

        } catch (Exception e) {
            try {
                if (s3KeyPdf != null) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucket).key(s3KeyPdf).build());
                }
            } catch (Exception cleanupEx) {
            }
            try {
                if (s3KeyFile != null) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucket).key(s3KeyFile).build());
                }
            } catch (Exception cleanupEx) {
            }
            throw new IOException("Kunde inte spara rapport: " + e.getMessage());
        }
    }

    private boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private boolean isPdf(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }

    private boolean isWord(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (
                contentType.equals("application/msword") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );
    }


    private String caseNumberSanitation(String caseNumber) {
        if (caseNumber.matches("^\\d{4}-\\d{6}$")) {
            return "K-" + caseNumber;
        } else {
            return caseNumber.toUpperCase();
        }
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public long getAmount() {
        return reportRepository.count();
    }

    public List<ReportResponse> getAllReportResponses() {
        return reportRepository.findAll().stream()
                .map(r -> new ReportResponse(r.getUuid(), r.getName(), r.getEvent()))
                .toList();
    }
}
