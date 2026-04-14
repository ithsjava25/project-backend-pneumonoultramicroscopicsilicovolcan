package org.example.crimearchive.DTO;

import jakarta.validation.constraints.NotBlank;
import org.example.crimearchive.validations.ValidCasenumber;

public record CreateReport(
        @NotBlank(message = "Måste ha en anledning till anmälan")
        String event,
        @NotBlank(message = "Måste ha ett namn på anmälaren")
        String name,
        @ValidCasenumber
        String caseNumber){

    public CreateReport() {
        this("", "","");
    }

    public CreateReport(String event, String name) {
        this(event, name, "");
    }
}
