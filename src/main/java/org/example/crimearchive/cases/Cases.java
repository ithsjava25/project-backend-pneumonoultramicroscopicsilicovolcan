package org.example.crimearchive.cases;

import jakarta.persistence.*;
import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.reports.Report;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
public class Cases {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @Column(unique = true, nullable = false)
    private String caseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status = CaseStatus.OPEN;

    @ManyToMany
    @JoinTable(
            name = "account_cases",
            joinColumns = @JoinColumn(name = "report_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "caseEntity")
    private Set<Report> reports = new HashSet<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<CaseEvent> events = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<CaseComment> comments = new ArrayList<>();


    public Cases() {
    }

    public Report addReport(CreateReport report) {
        Report newReport = new Report(
                UUID.randomUUID(),
                report.name(),
                report.event()
        );
        reports.add(newReport);
        newReport.setCaseEntity(this);
        return newReport;
    }

    public void addAccountToCase(Account account){
        accounts.add(account);
    }
    public void removeAccountFromCase(Account account){
        accounts.remove(account);
    }

    public Cases(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public Long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<Account> accounts) {
        this.accounts = accounts;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    public CaseStatus getStatus() {
        return status;
    }

    public void setStatus(CaseStatus status) {
        this.status = status;
    }

    public List<CaseEvent> getEvents() {
        return events;
    }

    public List<CaseComment> getComments() {
        return comments;
    }
}
