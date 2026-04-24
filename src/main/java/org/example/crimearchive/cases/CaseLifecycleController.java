package org.example.crimearchive.cases;

import org.example.crimearchive.permissions.PermissionService;
import org.example.crimearchive.polis.Account;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CaseLifecycleController {

    private final CaseLifecycleService lifecycleService;
    private final PermissionService permissionService;

    public CaseLifecycleController(CaseLifecycleService lifecycleService, PermissionService permissionService) {
        this.lifecycleService = lifecycleService;
        this.permissionService = permissionService;
    }

    @GetMapping("/{caseNumber}/status")
    public ResponseEntity<CaseStatusResponse> getStatus(@PathVariable String caseNumber,
                                                         @AuthenticationPrincipal Account user) {
        requireAccess(caseNumber, user);
        CaseStatus status = lifecycleService.getStatus(caseNumber);
        return ResponseEntity.ok(new CaseStatusResponse(caseNumber, status));
    }

    @PutMapping("/{caseNumber}/status")
    public ResponseEntity<Void> changeStatus(@PathVariable String caseNumber,
                                              @RequestParam CaseStatus status,
                                              @AuthenticationPrincipal Account user) {
        requireAccess(caseNumber, user);
        lifecycleService.changeStatus(caseNumber, status, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{caseNumber}/comments")
    public ResponseEntity<CaseCommentResponse> addComment(@PathVariable String caseNumber,
                                                           @RequestParam String content,
                                                           @AuthenticationPrincipal Account user) {
        requireAccess(caseNumber, user);
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kommentar får inte vara tom");
        }
        if (content.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kommentar får inte överstiga 2000 tecken");
        }
        CaseComment comment = lifecycleService.addComment(caseNumber, content, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(CaseCommentResponse.from(comment));
    }

    @GetMapping("/{caseNumber}/comments")
    public ResponseEntity<List<CaseCommentResponse>> getComments(@PathVariable String caseNumber,
                                                                   @AuthenticationPrincipal Account user) {
        requireAccess(caseNumber, user);
        List<CaseCommentResponse> comments = lifecycleService.getComments(caseNumber)
                .stream().map(CaseCommentResponse::from).toList();
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{caseNumber}/events")
    public ResponseEntity<List<CaseEventResponse>> getEvents(@PathVariable String caseNumber,
                                                               @AuthenticationPrincipal Account user) {
        requireAccess(caseNumber, user);
        List<CaseEventResponse> events = lifecycleService.getEvents(caseNumber)
                .stream().map(CaseEventResponse::from).toList();
        return ResponseEntity.ok(events);
    }

    private void requireAccess(String caseNumber, Account user) {
        if (!permissionService.canAccessCase(caseNumber, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Åtkomst nekad till ärende: " + caseNumber);
        }
    }
}
