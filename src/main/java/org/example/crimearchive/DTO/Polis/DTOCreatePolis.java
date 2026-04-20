package org.example.crimearchive.DTO.Polis;

import jakarta.validation.constraints.NotBlank;


public record DTOCreatePolis(
        @NotBlank(message = "Namn får inte vara tomt")
        String fullName,
        @NotBlank(message = "Roll får inte vara tomt")
        String profession,
        @NotBlank(message = "Avdelning får inte vara tomt")
        String department,
        @NotBlank(message = "Användarnamn får inte vara tomt")
        String username,
        @NotBlank(message = "Lösenord får inte vara tomt")
        String password,
        //Fixa en validering hit istället för att ha det i service
        @NotBlank(message = "Behörighet får inte vara tomt")
        String roles
) {
        public DTOCreatePolis() {
                this(null, null, null, null, null, null);
        }
}
