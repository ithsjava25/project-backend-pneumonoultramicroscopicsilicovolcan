package org.example.crimearchive.cases;

import jakarta.persistence.*;
import org.example.crimearchive.listeners.CaseListener;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.reports.Report;

import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(CaseListener.class)
@Table(name = "cases")
public class Cases {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String caseNumber;

    @ManyToMany
    @JoinTable(
        name = "case_accounts",
        joinColumns = @JoinColumn(name = "case_id"),
        inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL)
    private Set<Report> reports = new HashSet<>();

    public Cases() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void addAccountToCase(Account account) {
        this.accounts.add(account);
        account.getPermittedCases().add(this);
    }

    public void removeAccountFromCase(Account account) {
        this.accounts.remove(account);
        account.getPermittedCases().remove(this);
    }
}
