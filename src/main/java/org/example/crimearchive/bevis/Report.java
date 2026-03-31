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
    private String s3KeyPdf;
    private String s3KeyFile;


    public Report() {
    }

    public Report(UUID id, String name, String event, String s3KeyPdf, String s3KeyFile) {
        this.uuid = id;
        this.name = name;
        this.event = event;
        this.s3KeyPdf = s3KeyPdf;
        this.s3KeyFile = s3KeyFile;
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

    public String getS3KeyPdf() {
        return s3KeyPdf;
    }

    public void setS3KeyPdf(String s3KeyPdf) {
        this.s3KeyPdf = s3KeyPdf;
    }
    public String getS3KeyFile() {
        return s3KeyFile;
    }

    public void setS3KeyFile(String s3KeyFile) {
        this.s3KeyFile = s3KeyFile;
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
