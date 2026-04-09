package org.example.crimearchive.cases;

import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.example.crimearchive.reports.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

//
//    public List<Report> hasPermission(Long accountId){
//    List<String> permittedCaseNumbers = permissionRepository.findAllPermittedReports(accountId);
//    return simpleRepository.findAllByCaseNumberIn(permittedCaseNumbers);
//    }
}
