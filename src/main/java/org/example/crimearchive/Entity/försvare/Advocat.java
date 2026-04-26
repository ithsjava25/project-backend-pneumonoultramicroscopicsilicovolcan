package org.example.crimearchive.Entity.försvare;
import jakarta.persistence.*;
import org.example.crimearchive.audit.Auditable;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "advocat")
public class Advocat extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String name;

    private String knumber;

    private LocalDate appointedTime;

    private ZonedDateTime crimeTime;

    // Getters and Setters if needed, or use Lombok if available
}
