package org.example.crimearchive.cases;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseEventRepository extends JpaRepository<CaseEvent, Long> {
    List<CaseEvent> findByCaseEntityOrderByCreatedAtAsc(Cases caseEntity);
}
