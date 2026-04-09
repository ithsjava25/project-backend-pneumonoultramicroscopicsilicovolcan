package org.example.crimearchive.entities.bevis;
import jakarta.persistence.*;

@Entity
@Table(name = "brottsplats")
public class Brottsplats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String adress;

    @Column(nullable = false)
    private String stad;

    private String beskrivning;

    public Brottsplats() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAdress() { return adress; }
    public void setAdress(String adress) { this.adress = adress; }
    public String getStad() { return stad; }
    public void setStad(String stad) { this.stad = stad; }
    public String getBeskrivning() { return beskrivning; }
    public void setBeskrivning(String beskrivning) { this.beskrivning = beskrivning; }
}
