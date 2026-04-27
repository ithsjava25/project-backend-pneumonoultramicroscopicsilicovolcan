package org.example.crimearchive.Entity.Golare;


import jakarta.persistence.*;
import org.example.crimearchive.audit.Auditable;

@Entity
@Table(name = "golare")
public class Golare extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namn;

    @Column(nullable = false)
    private String Telephone;

    @Column(nullable = false)
    private String email;

}
