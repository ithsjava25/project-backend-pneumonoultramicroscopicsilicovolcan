package org.example.crimearchive.DTO.Golare;

import jakarta.validation.constraints.NotBlank;

public record DTOGolare (
        long id,
        String name,
        String email,
        String phone
){
}
