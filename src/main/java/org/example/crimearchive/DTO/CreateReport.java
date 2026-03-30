package org.example.crimearchive.DTO;

import jakarta.validation.constraints.NotBlank;

public record CreateReport(
        @NotBlank(message = "Måste ha en anledning till anmälan")
        String event,

        @NotBlank(message = "Måste ha ett namn på anmälaren")
        String name,

        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        String location,
        String description,
        String policeName,
        String lawyerName,
        String prosecutorName
) {
    public CreateReport() {
        this("", "", "", "", "", "", "", "", "", "", "");
    }
}
