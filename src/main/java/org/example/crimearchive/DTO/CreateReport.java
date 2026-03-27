package org.example.crimearchive.DTO;

import jakarta.validation.constraints.NotBlank;

public record CreateReport(
        @NotBlank(message = "Måste ha en anledning till anmälan")
        String event,
        @NotBlank(message = "Måste ha ett namn på anmälaren")
        String name) {

    public CreateReport() {
        this("", "");
    }
}
