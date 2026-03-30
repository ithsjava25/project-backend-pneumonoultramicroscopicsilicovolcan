package org.example.crimearchive.controllers;

import org.example.crimearchive.bevis.Report;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class ReportPrototypeController {

    @GetMapping("/reports/prototype")
    public String showPrototypeReport(Model model) {

        Report report = new Report();
        report.setUuid(UUID.randomUUID());

        report.setName("Mona Sahlén");
        report.setFirstName("Mona");
        report.setLastName("Sahlén");
        report.setPhoneNumber("070-123 45 67");
        report.setEmail("mona.sahlen@email.se");

        report.setEvent("Kränkande uttalande på allmän plats");
        report.setLocation("ICA Focus, Gårda");
        report.setDescription("""
        Målsäganden, som är anställd på ICA Focus i Gårda, uppger att hon i samband
        med händelsen blivit utsatt för ett kränkande uttalande av innebörd att hon
        skulle ha "bättre mustasch än Håkan Juholt".

        Enligt uppgift har uttalandet medfört betydande psykiskt lidande för
        målsäganden och uppges ha lett till utdraget självskadebeteende samt
        behandlings- och terapikostnader om cirka 16 000 kronor.

        Den utpekade personen förnekar enligt uppgift inte att uttalandet fällts,
        men uppges ha anfört att målsäganden är en "snöflinga" och att hon kan
        "spola ner sina tårar i toaletten".
        """);

        report.setCreatedAt(LocalDateTime.now());

        report.setPoliceName("Insp. Sara Lindberg");
        report.setLawyerName("Adv. Johan Ek");
        report.setProsecutorName("Kammaråklagare Maria Holm");

        report.setPoliceAuthority("VÄSTRA GÖTALANDS LÄN");
        report.setCaseNumber("012-12023");
        report.setUnit("9B3");
        report.setAuthorityCode("0942");

        model.addAttribute("report", report);
        return "report-prototype";
    }
}
