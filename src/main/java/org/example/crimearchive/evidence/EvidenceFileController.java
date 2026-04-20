package org.example.crimearchive.evidence;

import org.springframework.http.ResponseEntity;
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
    public List<EvidenceFile> getByCaseNumber(@PathVariable String caseNumber) {
        return evidenceFileService.getByCaseNumber(caseNumber);
    }

    @GetMapping("/case/{caseNumber}/latest")
    public List<EvidenceFile> getLatestVersions(@PathVariable String caseNumber) {
        return evidenceFileService.getLatestVersionsByCaseNumber(caseNumber);
    }

    @GetMapping("/group/{groupId}/history")
    public List<EvidenceFile> getVersionHistory(@PathVariable UUID groupId) {
        return evidenceFileService.getVersionHistory(groupId);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
        return evidenceFileService.downloadPdf(id);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID id) {
        return evidenceFileService.downloadFile(id);
    }
}
