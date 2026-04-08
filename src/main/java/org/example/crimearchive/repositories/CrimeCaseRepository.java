package org.example.crimearchive.repositories;

import org.example.crimearchive.entities.cases.CrimeCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrimeCaseRepository extends JpaRepository<CrimeCase, Long> {
    Optional<CrimeCase> findByCaseNumber(String caseNumber);
}
