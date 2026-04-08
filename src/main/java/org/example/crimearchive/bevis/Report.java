package org.example.crimearchive.bevis;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;
import java.util.UUID;

@Entity
public class Report {

    @Id
    private UUID uuid;
    private String name;
    private String event;
    private String caseNumber;


    public Report() {
    }

    public Report(UUID id, String name, String event, String caseNumber) {
        this.uuid = id;
        this.name = name;
        this.event = event;
        this.caseNumber = caseNumber;
    }

//    public Set<Account> getPermittedViewers() {
//        return permittedViewers;
//    }
//
//    public void setPermittedViewers(Set<Account> permittedViewers) {
//        this.permittedViewers = permittedViewers;
//    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(uuid, report.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
