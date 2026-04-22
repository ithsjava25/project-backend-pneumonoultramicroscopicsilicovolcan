package org.example.crimearchive.cases;

import org.example.crimearchive.exceptions.NotFoundException;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.example.crimearchive.reports.Report;
import org.example.crimearchive.reports.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void addAccountToCase(Long accountId, String caseNumber) {
        Optional<Account> account = userRepository.findById(accountId);
        if (account.isEmpty()) throw new NotFoundException("Account not found");
        Optional<Cases> cases = casesRepository.findFirstByCaseNumber(caseNumber);
        if (cases.isEmpty()) throw new NotFoundException("Case does not exist");
        if (cases.get().getAccounts().stream().anyMatch(acc -> acc.getId().equals(accountId))) {
            cases.get().removeAccountFromCase(account.get());
        } else {
            cases.get().addAccountToCase(account.get());
        }
        casesRepository.save(cases.get());
    }

    @Transactional
    public void addAccountToCase(Long accountId, Long caseId) {
        Optional<Account> account = userRepository.findById(accountId);
        if (account.isEmpty()) throw new NotFoundException("Account not found with id: " + accountId);
        Optional<Cases> cases = casesRepository.findById(caseId);
        if (cases.isEmpty()) throw new NotFoundException("Case not found with id: " + caseId);
        cases.get().addAccountToCase(account.get());
        casesRepository.save(cases.get());
    }

    public List<Cases> getAllCases() {
        return casesRepository.findAll();
    }

    public List<Account> getAllPoliceForCase(String casenumber) {
        return casesRepository.findFirstByCaseNumber(casenumber)
                .orElseThrow(() -> new NotFoundException("Case not found: " + casenumber))
                .getAccounts().stream().toList();
    }

    public void saveCase(Cases newCase){
        casesRepository.save(newCase);
    }

    public List<Cases> getAuthzCases(long accountId) {
        return casesRepository.findByAccountsId(accountId);
    }

    public Report getReport(Cases caseId) {
        return reportRepository.getReportByCaseEntity(caseId);
    }

    @Transactional(readOnly = true)
    public Set<Report> getReportSet(Long caseId) {
        return casesRepository.findById(caseId).orElseThrow(() -> new NotFoundException("Case not found with id: " + caseId))
                .getReports();
    }

    @Transactional(readOnly = true)
    public Set<Report> getReportSet(String caseNumber) {
        Long id = caseIdFromCaseNumber(caseNumber);
        return casesRepository.findById(id).orElseThrow(() -> new NotFoundException("Case not found with case number: " + caseNumber))
                .getReports();
    }

    public Map<String, Set<Report>> getReportsWithCaseNumber(List<Cases> grantedCases) {
        Map<String, Set<Report>> repos = grantedCases.stream().collect(Collectors.toMap(
                cases -> cases.getCaseNumber(), c2 -> c2.getReports()
        ));
        return repos;
    }

    public List<Cases> getAllUnSignedCases() {
        return casesRepository.findAllByAccountsEmpty();
    }


    public Long caseIdFromCaseNumber(String casenumber) {
        return casesRepository.findFirstByCaseNumber(casenumber)
                .orElseThrow(() -> new NotFoundException("Case not found: " + casenumber))
                .getId();
    }

    public Optional<Cases> getCaseFromCaseNumber(String casenumber){
        return casesRepository.findFirstByCaseNumber(casenumber);
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
