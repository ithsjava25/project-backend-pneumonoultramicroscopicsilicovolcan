package org.example.crimearchive.Entity.Åklagare;


import jakarta.persistence.*;
import org.example.crimearchive.audit.Auditable;

@Entity
@Table(name = "Åklagare")
public class Åklagare extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namn;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String CompanyName;
}
