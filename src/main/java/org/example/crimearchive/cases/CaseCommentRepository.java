package org.example.crimearchive.cases;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseCommentRepository extends JpaRepository<CaseComment, Long> {
    List<CaseComment> findByCaseEntityOrderByCreatedAtAsc(Cases caseEntity);
}
