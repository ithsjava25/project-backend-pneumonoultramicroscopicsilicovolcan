package org.example.crimearchive.evidence;

import org.example.crimearchive.polis.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/evidence")
public class EvidenceFileController {

    private final EvidenceFileService evidenceFileService;

    public EvidenceFileController(EvidenceFileService evidenceFileService) {
        this.evidenceFileService = evidenceFileService;
    }

    @GetMapping("/case/{caseNumber}")
    @PreAuthorize("@caseSecurity.canAccessCase(#caseNumber, principal)")
    public List<EvidenceFileResponse> getByCaseNumber(@PathVariable String caseNumber) {
        return evidenceFileService.getByCaseNumber(caseNumber)
                .stream().map(EvidenceFileResponse::from).toList();
    }

    @GetMapping("/case/{caseNumber}/latest")
    @PreAuthorize("@caseSecurity.canAccessCase(#caseNumber, principal)")
    public List<EvidenceFileResponse> getLatestVersions(@PathVariable String caseNumber) {
        return evidenceFileService.getLatestVersionsByCaseNumber(caseNumber)
                .stream().map(EvidenceFileResponse::from).toList();
    }

    @GetMapping("/group/{groupId}/history")
    public List<EvidenceFileResponse> getVersionHistory(@PathVariable UUID groupId,
                                                        @AuthenticationPrincipal Account currentUser) {
        return evidenceFileService.getVersionHistory(groupId, currentUser)
                .stream().map(EvidenceFileResponse::from).toList();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id,
                                              @AuthenticationPrincipal Account currentUser) {
        return evidenceFileService.downloadPdf(id, currentUser);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID id,
                                               @AuthenticationPrincipal Account currentUser) {
        return evidenceFileService.downloadFile(id, currentUser);
    }
}
