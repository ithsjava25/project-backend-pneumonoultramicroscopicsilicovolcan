package org.example.crimearchive.cases;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CaseLifecycleService {

    private final CasesRepository casesRepository;
    private final CaseEventRepository eventRepository;
    private final CaseCommentRepository commentRepository;

    public CaseLifecycleService(CasesRepository casesRepository,
                                CaseEventRepository eventRepository,
                                CaseCommentRepository commentRepository) {
        this.casesRepository = casesRepository;
        this.eventRepository = eventRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void initCase(Cases cases, String performedBy) {
        cases.setStatus(CaseStatus.OPEN);
        eventRepository.save(new CaseEvent(cases, CaseEventType.CASE_CREATED,
                "Ärende " + cases.getCaseNumber() + " skapades", performedBy));
    }

    @Transactional
    public void onHandlerAssigned(Cases cases, String handlerName, String performedBy) {
        eventRepository.save(new CaseEvent(cases, CaseEventType.HANDLER_ASSIGNED,
                "Handläggare " + handlerName + " tilldelades ärendet", performedBy));
        if (cases.getStatus() == CaseStatus.OPEN) {
            updateStatus(cases, CaseStatus.ASSIGNED, performedBy);
        }
    }

    @Transactional
    public void onHandlerRemoved(Cases cases, String handlerName, String performedBy) {
        eventRepository.save(new CaseEvent(cases, CaseEventType.HANDLER_REMOVED,
                "Handläggare " + handlerName + " togs bort från ärendet", performedBy));
        // Only revert to OPEN from ASSIGNED — later states (IN_PROGRESS, CLOSED) are not disturbed by handler removal.
        if (cases.getAccounts().isEmpty() && cases.getStatus() == CaseStatus.ASSIGNED) {
            updateStatus(cases, CaseStatus.OPEN, performedBy);
        }
    }

    @Transactional
    public void onDocumentUploaded(Cases cases, String filename, String performedBy) {
        eventRepository.save(new CaseEvent(cases, CaseEventType.DOCUMENT_UPLOADED,
                "Dokument laddades upp: " + filename, performedBy));
    }

    @Transactional
    public CaseComment addComment(String caseNumber, String content, String author) {
        if (content == null || content.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kommentar får inte vara tom");
        }
        if (content.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kommentar får inte överstiga 2000 tecken");
        }
        Cases cases = findCase(caseNumber);
        CaseComment comment = commentRepository.save(new CaseComment(cases, content, author));
        eventRepository.save(new CaseEvent(cases, CaseEventType.COMMENT_ADDED,
                author + " lade till en kommentar", author));
        return comment;
    }

    @Transactional
    public void changeStatus(String caseNumber, CaseStatus newStatus, String performedBy) {
        Cases cases = findCase(caseNumber);
        if (cases.getStatus() == newStatus) return;
        updateStatus(cases, newStatus, performedBy);
    }

    public List<CaseEvent> getEvents(String caseNumber) {
        Cases cases = findCase(caseNumber);
        return eventRepository.findByCaseEntityOrderByCreatedAtAsc(cases);
    }

    public List<CaseComment> getComments(String caseNumber) {
        Cases cases = findCase(caseNumber);
        return commentRepository.findByCaseEntityOrderByCreatedAtAsc(cases);
    }

    public CaseStatus getStatus(String caseNumber) {
        return findCase(caseNumber).getStatus();
    }

    private void updateStatus(Cases cases, CaseStatus newStatus, String performedBy) {
        CaseStatus previous = cases.getStatus();
        cases.setStatus(newStatus);
        eventRepository.save(new CaseEvent(cases, CaseEventType.STATUS_CHANGED,
                "Status ändrades från " + previous + " till " + newStatus, performedBy));
    }

    private Cases findCase(String caseNumber) {
        return casesRepository.findFirstByCaseNumber(caseNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ärende hittades inte: " + caseNumber));
    }
}
