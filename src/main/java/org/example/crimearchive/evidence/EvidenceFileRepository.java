package org.example.crimearchive.evidence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceFileRepository extends JpaRepository<EvidenceFile, UUID> {
    List<EvidenceFile> findByCaseNumber(String caseNumber);
    List<EvidenceFile> findByGroupIdOrderByVersionAsc(UUID groupId);
    Optional<EvidenceFile> findTopByGroupIdOrderByVersionDesc(UUID groupId);

    @Query("SELECT e FROM EvidenceFile e WHERE e.caseNumber = :caseNumber AND e.version = (SELECT MAX(e2.version) FROM EvidenceFile e2 WHERE e2.groupId = e.groupId)")
    List<EvidenceFile> findLatestVersionsByCaseNumber(String caseNumber);
}
