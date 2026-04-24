package org.example.crimearchive.permissions;

import org.example.crimearchive.cases.CaseLifecycleService;
import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.cases.CaseStatus;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

@Component("caseSecurity")
public class PermissionService {

    private final CaseService caseService;
    private final UserService userService;
    private final CaseLifecycleService lifecycleService;


    public PermissionService(CaseService caseService,
                             UserService userService, CaseLifecycleService lifecycleService) {
        this.caseService = caseService;
        this.userService = userService;
        this.lifecycleService = lifecycleService;

    }

    public boolean canAccessCase(String caseNumber, Account currentUser) {
        if (caseNumber == null || currentUser == null) return false;
        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) return true;
        return caseService.accountIdConnectedWithCaseId(caseNumber, currentUser.getId());
    }

    public Either<ModelAttributesDTO,ModelAttributesRedacted> getGrantedModelAttributes(String casenumber, Account user){
        if(canAccessCase(casenumber, user)){
            return Either.left(new ModelAttributesDTO(
                    caseService.getAllPoliceForCase(casenumber),
                    caseService.getReportSet(casenumber),
                    CaseStatus.values(),
                    lifecycleService.getStatus(casenumber),
                    lifecycleService.getEvents(casenumber),
                    lifecycleService.getComments(casenumber)
            ));
        }else{
            return Either.right(new ModelAttributesRedacted(
                "<CENSURSERAD>",
                    "<CENSURSERAD>",
                    "<CENSURSERAD>",
                    lifecycleService.getStatus(casenumber),
                    lifecycleService.getEvents(casenumber),
                    lifecycleService.getComments(casenumber))
            );
        }
    }
//
//    public boolean canAccessCase(String casenumber, String username){
//        if (casenumber == null) return false;
//        Account currentUser = userService.getUserByUsernameIfExists(username);
//        return caseService.accountIdConnectedWithCaseId(casenumber, currentUser.getId());
//    }

}
