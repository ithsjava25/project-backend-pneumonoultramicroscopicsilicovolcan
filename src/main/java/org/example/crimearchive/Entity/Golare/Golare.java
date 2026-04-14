package org.example.crimearchive.Entity.Golare;


import jakarta.persistence.*;

@Entity
@Table(name = "golare")
public class Golare {

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
