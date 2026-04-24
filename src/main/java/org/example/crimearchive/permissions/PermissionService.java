package org.example.crimearchive.permissions;

import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.polis.Account;
import org.springframework.stereotype.Component;

@Component("caseSecurity")
public class PermissionService {

    private final CaseService caseService;

    public PermissionService(CaseService caseService) {
        this.caseService = caseService;
    }

    public boolean canAccessCase(String caseNumber, Account currentUser) {
        if (caseNumber == null || currentUser == null) return false;
        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) return true;
        return caseService.accountIdConnectedWithCaseId(caseNumber, currentUser.getId());
    }
}
