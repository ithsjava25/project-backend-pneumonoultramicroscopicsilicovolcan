package org.example.crimearchive.cases;

import org.example.crimearchive.polis.Account;
import org.example.crimearchive.reports.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("permittedFilesChecker")
public class CaseService {

    private final ReportRepository reportRepository;
    private final CasesRepository casesRepository;

    public CaseService(ReportRepository reportRepository, CasesRepository casesRepository) {
        this.reportRepository = reportRepository;
        this.casesRepository = casesRepository;
    }

    public void addAccountToCase(Account account, String caseNumber){
        Optional<Cases> cases = casesRepository.findFirstByCaseNumber(caseNumber);
        if(cases.isPresent()){
            cases.get().addAccountToCase(account);
            casesRepository.save(cases.get());
        }
    }

//
//    public List<Report> hasPermission(Long accountId){
//    List<String> permittedCaseNumbers = permissionRepository.findAllPermittedReports(accountId);
//    return simpleRepository.findAllByCaseNumberIn(permittedCaseNumbers);
//    }
}
