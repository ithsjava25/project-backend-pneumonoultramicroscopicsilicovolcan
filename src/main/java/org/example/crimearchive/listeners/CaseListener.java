package org.example.crimearchive.listeners;

import jakarta.persistence.PrePersist;
import org.example.crimearchive.KNumberService;
import org.example.crimearchive.config.SpringContext;
import org.example.crimearchive.entities.cases.CrimeCase;
import org.example.crimearchive.cases.Cases;

public class CaseListener {

    @PrePersist
    public void setKNumber(Object entity) {
        if (entity instanceof CrimeCase crimeCase) {
            if (crimeCase.getCaseNumber() == null || crimeCase.getCaseNumber().isEmpty()) {
                KNumberService kNumberService = SpringContext.getBean(KNumberService.class);
                crimeCase.setCaseNumber(kNumberService.getKNumber());
            }
        }
    }
}
