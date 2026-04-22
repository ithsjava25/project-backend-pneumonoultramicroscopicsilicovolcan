package org.example.crimearchive.mappers;

import org.example.crimearchive.dto.knumber.DTOKnumber;
import org.example.crimearchive.entities.cases.CrimeCase;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class CrimeCaseMapper {

    public DTOKnumber toDTO(CrimeCase crimeCase) {
        if (crimeCase == null) {
            return null;
        }
        return new DTOKnumber(
                crimeCase.getId(),
                crimeCase.getCaseNumber(),
                crimeCase.getDescription(),
                crimeCase.getCreatedAt() != null ? crimeCase.getCreatedAt().atZone(ZoneId.systemDefault()) : null
        );
    }
}
