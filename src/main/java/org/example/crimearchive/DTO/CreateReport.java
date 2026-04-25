package org.example.crimearchive.DTO;

import jakarta.validation.constraints.NotBlank;
import org.example.crimearchive.validations.ValidCasenumber;

public record CreateReport(
        @NotBlank(message = "Brottsplats måste anges")
        String event,
        @NotBlank(message = "Polis måste anges")
        String name,
        @ValidCasenumber
        String caseNumber,
        @NotBlank(message = "Vittne måste anges")
        String witness,
        @NotBlank(message = "Offer måste anges")
        String victim
) {

    public CreateReport() {
        this("", "", "", "", "");
    }

    public CreateReport(String event, String name) {
        this(event, name, "", "", "");
    }

    public CreateReport(String event, String name, String caseNumber) {
        this(event, name, caseNumber, "", "");
    }
}
