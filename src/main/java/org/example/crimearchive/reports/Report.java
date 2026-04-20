package org.example.crimearchive.reports;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.example.crimearchive.cases.Cases;

import java.util.Objects;
import java.util.UUID;

@Entity
public class Report {

    @Id
    private UUID uuid;
    private String name;
    private String event;
    @ManyToOne
    @JoinColumn(name = "case_id")
    private Cases caseEntity;

    public Cases getCaseEntity() {
        return caseEntity;
    }

    public void setCaseEntity(Cases caseEntity) {
        this.caseEntity = caseEntity;
    }

    public Report() {
    }

    public Report(UUID id, String name, String event, Cases caseEntity) {
        this.uuid = id;
        this.name = name;
        this.event = event;
        this.caseEntity = caseEntity;
    }

    public Report(UUID id, String name, String event) {
        this(id, name, event, (Cases) null);
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
