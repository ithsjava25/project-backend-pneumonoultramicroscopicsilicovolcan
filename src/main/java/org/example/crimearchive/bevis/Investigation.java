package org.example.crimearchive.bevis;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.example.crimearchive.polis.Account;

import java.util.Set;

@Entity
public class Investigation {
    @Id
    private long id;

    private String caseNumber;

//    @ManyToMany(mappedBy = "permittedCases")
//    private Set<Account> permittedViewers = new HashSet<>();


    public Investigation() {
    }

    public Investigation(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public Set<Account> getPermittedViewers() {
        return permittedViewers;
    }

    public void setPermittedViewers(Set<Account> permittedViewers) {
        this.permittedViewers = permittedViewers;
    }
}
