package org.example.crimearchive.målsägare;


import jakarta.persistence.*;

@Entity
@Table(name="Brottsoffer")
public class Brottsoffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false)
    private String Knummer;
}
