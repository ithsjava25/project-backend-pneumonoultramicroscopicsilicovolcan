package org.example.crimearchive.entities.defense;


import jakarta.persistence.*;


@Entity
@Table(name = "advokat")
public class Advocat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namn;

    @Column(nullable = false)
    public String telefon;

    @Column(nullable = false)
    public String email;

    @Column(nullable = false, name = "Company")
    public String Company;
}
