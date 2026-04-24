package org.example.crimearchive.permissions;

import org.example.crimearchive.cases.CaseComment;
import org.example.crimearchive.cases.CaseEvent;
import org.example.crimearchive.cases.CaseStatus;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.reports.Report;

import java.util.List;
import java.util.Set;

public record ModelAttributesDTO(
        List<Account> assignedPolice,
        Set<Report> reportList,
        CaseStatus[] setCaseStatus,
        CaseStatus getCaseStatus,
        List<CaseEvent> caseEvents,
        List<CaseComment> caseComments

) {
}
