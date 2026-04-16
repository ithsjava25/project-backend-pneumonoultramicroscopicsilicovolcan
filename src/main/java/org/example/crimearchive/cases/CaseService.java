package org.example.crimearchive.cases;

import org.example.crimearchive.permissions.NullAuthzDeniedHandler;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.example.crimearchive.reports.Report;
import org.example.crimearchive.reports.ReportRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.HandleAuthorizationDenied;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service("permittedFilesChecker")
public class CaseService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final CasesRepository casesRepository;

    public CaseService(ReportRepository reportRepository, CasesRepository casesRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.casesRepository = casesRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('admin')")
    @HandleAuthorizationDenied(handlerClass = NullAuthzDeniedHandler.class)
    public void addAccountToCase(Long accountId, String caseNumber){
        Optional<Account> account = userRepository.findById(accountId);
        if (account.isEmpty()) throw new RuntimeException("account not found");
        Account foundAccount = account.get();
        Optional<Cases> cases = casesRepository.findFirstByCaseNumber(caseNumber);
        if(cases.isPresent()){
            cases.get().addAccountToCase(foundAccount);
            casesRepository.save(cases.get());
        }
    }

    public List<Cases> getAuthzCases(long accountId) {
        return casesRepository.findByAccountsId(accountId);
    }

    public Report getReports(Cases caseId) {
        return reportRepository.getReportByCaseEntity(caseId);
    }

    public Map<String, Set<Report>> getReportsWithCaseNumber(List<Cases> grantedCases) {
        Map<String, Set<Report>> repos = grantedCases.stream().collect(Collectors.toMap(
                cases -> cases.getCaseNumber(), c2 -> c2.getReports()
        ));
        return repos;
    }

//
//    public List<Report> hasPermission(Long accountId){
//    List<String> permittedCaseNumbers = permissionRepository.findAllPermittedReports(accountId);
//    return simpleRepository.findAllByCaseNumberIn(permittedCaseNumbers);
//    }
}
