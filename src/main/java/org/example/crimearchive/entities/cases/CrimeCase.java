package org.example.crimearchive.entities.cases;

import jakarta.persistence.*;
import org.example.crimearchive.listeners.CaseListener;
import org.example.crimearchive.entities.police.Aina;
import org.example.crimearchive.entities.bevis.Bevis;
import org.example.crimearchive.entities.bevis.Brottsplats;
import org.example.crimearchive.entities.prosecution.Åklagare;
import org.example.crimearchive.entities.vittne.Vittne;
import org.example.crimearchive.entities.brottsoffer.Brottsoffer;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(CaseListener.class)
@Table(name = "crime_case")
public class CrimeCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String caseNumber;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "aklagare_id")
    private Åklagare aklagare;

    @ManyToOne
    @JoinColumn(name = "brottsplats_id")
    private Brottsplats brottsplats;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "case_id")
    private Set<Bevis> bevis = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "case_police",
        joinColumns = @JoinColumn(name = "case_id"),
        inverseJoinColumns = @JoinColumn(name = "police_id")
    )
    private Set<Aina> poliser = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "case_victim",
        joinColumns = @JoinColumn(name = "case_id"),
        inverseJoinColumns = @JoinColumn(name = "victim_id")
    )
    private Set<Brottsoffer> brottsoffer = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "case_witness",
        joinColumns = @JoinColumn(name = "case_id"),
        inverseJoinColumns = @JoinColumn(name = "witness_id")
    )
    private Set<Vittne> vittnen = new HashSet<>();

    public CrimeCase() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Åklagare getAklagare() { return aklagare; }
    public void setAklagare(Åklagare aklagare) { this.aklagare = aklagare; }
    public Brottsplats getBrottsplats() { return brottsplats; }
    public void setBrottsplats(Brottsplats brottsplats) { this.brottsplats = brottsplats; }
    public Set<Aina> getPoliser() { return poliser; }
    public void setPoliser(Set<Aina> poliser) { this.poliser = poliser; }
    public Set<Bevis> getBevis() { return bevis; }
    public void setBevis(Set<Bevis> bevis) { this.bevis = bevis; }
    public Set<Brottsoffer> getBrottsoffer() { return brottsoffer; }
    public void setBrottsoffer(Set<Brottsoffer> brottsoffer) { this.brottsoffer = brottsoffer; }
    public Set<Vittne> getVittnen() { return vittnen; }
    public void setVittnen(Set<Vittne> vittnen) { this.vittnen = vittnen; }
}
