package org.example.crimearchive.bevis;

import jakarta.persistence.*;
import org.example.crimearchive.polis.Account;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Cases {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;
    @Column(unique = true, nullable = false)
    private String caseNumber;

    @ManyToMany
    @JoinTable(
            name = "account_cases",
            joinColumns = @JoinColumn(name = "report_id"),
            inverseJoinColumns = @JoinColumn(name = "account_id")
    )
    private Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "caseEntity")
    private Set<Report> reports = new HashSet<>();


    public Cases() {
    }

    public Cases(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public long getId() {
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
}
