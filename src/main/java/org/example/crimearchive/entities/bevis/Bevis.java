package org.example.crimearchive.entities.bevis;
import jakarta.persistence.*;

@Entity
@Table(name = "bevis")
public class Bevis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String typ;

    @Column(nullable = false)
    private String beskrivning;

    @Column(name = "upptackt_datum")
    private java.time.LocalDateTime upptacktDatum;

    public Bevis() {
        this.upptacktDatum = java.time.LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTyp() { return typ; }
    public void setTyp(String typ) { this.typ = typ; }
    public String getBeskrivning() { return beskrivning; }
    public void setBeskrivning(String beskrivning) { this.beskrivning = beskrivning; }
    public java.time.LocalDateTime getUpptacktDatum() { return upptacktDatum; }
    public void setUpptacktDatum(java.time.LocalDateTime upptacktDatum) { this.upptacktDatum = upptacktDatum; }
}
