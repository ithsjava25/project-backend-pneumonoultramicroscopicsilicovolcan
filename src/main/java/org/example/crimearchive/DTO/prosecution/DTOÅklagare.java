package org.example.crimearchive.dto.prosecution;

public record DTOÅklagare(
        long id,
        String name,
        String email,
        String phone,
        String company,
        String KNumber
) {
}
