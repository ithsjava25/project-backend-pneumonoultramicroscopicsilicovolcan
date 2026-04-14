package org.example.crimearchive.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DurTreReportsPrototypeController {

    @GetMapping("/durtre-reports-prototype")
    public String showDurTreReportsPrototype() {
        return "durtre-reports-prototype";
    }
}
