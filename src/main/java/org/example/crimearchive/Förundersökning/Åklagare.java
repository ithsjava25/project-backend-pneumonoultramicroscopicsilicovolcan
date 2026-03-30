package org.example.crimearchive.Förundersökning;


import jakarta.persistence.*;
import lombok.extern.apachecommons.CommonsLog;

@Entity
@Table(name = "Åklagare")
public class Åklagare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namn;

    @Column(nullable = false)
    private String klag;

    @Column(nullable = false)
    public String telephone;

    @Column(nullable = false)
    public String email;

    @Column(nullable = false)
    public String password;
}
