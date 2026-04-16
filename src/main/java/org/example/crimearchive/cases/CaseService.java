package org.example.crimearchive.cases;

import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.example.crimearchive.reports.Report;
import org.example.crimearchive.reports.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CaseService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final CasesRepository casesRepository;

    public CaseService(ReportRepository reportRepository, CasesRepository casesRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.casesRepository = casesRepository;
        this.userRepository = userRepository;
    }

    public void addAccountToCase(Long accountId, String caseNumber) {
        Optional<Account> account = userRepository.findById(accountId);
        if (account.isEmpty()) throw new RuntimeException("account not found");
        Optional<Cases> cases = casesRepository.findFirstByCaseNumber(caseNumber);
        if (cases.isEmpty()) throw new RuntimeException("Case does not exist");
        cases.get().addAccountToCase(account.get());
        casesRepository.save(cases.get());
    }

    public List<Cases> getAuthzCases(long accountId) {
        return casesRepository.findByAccountsId(accountId);
    }

    public Report getReport(Cases caseId) {
        return reportRepository.getReportByCaseEntity(caseId);
    }

    public Set<Report> getReportSet(Long caseId) {
        var caseSet = casesRepository.findById(caseId);
        if (caseSet.isEmpty()) throw new RuntimeException("Resource not found");
        return caseSet.get().getReports();
    }

    public Set<Report> getReportSet(String caseNumber) {
        Long id = caseIdFromCaseNumber(caseNumber);
        return casesRepository.findById(id).orElseThrow().getReports();
    }

    public Map<String, Set<Report>> getReportsWithCaseNumber(List<Cases> grantedCases) {
        Map<String, Set<Report>> repos = grantedCases.stream().collect(Collectors.toMap(
                cases -> cases.getCaseNumber(), c2 -> c2.getReports()
        ));
        return repos;
    }


    public Long caseIdFromCaseNumber(String casenumber) {
        return casesRepository.findIdByCaseNumberContaining(casenumber);
    }

    public boolean accountIdConnectedWithCaseId(String caseNumber, Long accountId) {
        return casesRepository.existsByCaseNumberAndAccounts_Id(caseNumber, accountId);
    }

//
//    public List<Report> hasPermission(Long accountId){
//    List<String> permittedCaseNumbers = permissionRepository.findAllPermittedReports(accountId);
//    return simpleRepository.findAllByCaseNumberIn(permittedCaseNumbers);
//    }
}
