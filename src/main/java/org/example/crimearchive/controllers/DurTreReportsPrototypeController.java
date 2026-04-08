package org.example.crimearchive.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class DurTreReportsPrototypeController {

    @GetMapping("/durtre-reports-prototype")
    public String showDurTreReportsPrototype(Model model) {
        model.addAttribute("handlaggare", List.of(
                "Insp. Sara Lindberg",
                "Insp. Johan Berg",
                "Insp. Lina Karlsson",
                "Utredare M. Andersson",
                "Kommissarie P. Holm"
        ));

        model.addAttribute("grupper", List.of(
                "Grova brott",
                "Bedrägeri",
                "Relationsvåld",
                "Ungdomsgrupp",
                "Spaning",
                "Jourgrupp"
        ));

        model.addAttribute("brottstyper", List.of(
                "Misshandel",
                "Olaga hot",
                "Ofredande",
                "Bedrägeri",
                "Stöld",
                "Skadegörelse",
                "Narkotikabrott",
                "Rån"
        ));

        model.addAttribute("avdelningar", List.of(
                "City Syd",
                "City Nord",
                "Väster",
                "Öster",
                "Regional utredning"
        ));

        model.addAttribute("results", List.of(
                Map.of(
                        "caseNumber", "K-2026-000184",
                        "crime", "Ofredande",
                        "latest", "2026-03-14 09:42",
                        "officer", "Insp. Sara Lindberg",
                        "suspect", "Agneta Lindström",
                        "department", "City Syd",
                        "status", "Öppen"
                ),
                Map.of(
                        "caseNumber", "K-2026-000172",
                        "crime", "Bedrägeri",
                        "latest", "2026-03-13 16:08",
                        "officer", "Utredare M. Andersson",
                        "suspect", "Anna Holm",
                        "department", "Regional utredning",
                        "status", "Pågående"
                )
        ));

        return "durtre-reports-prototype";
    }
}
