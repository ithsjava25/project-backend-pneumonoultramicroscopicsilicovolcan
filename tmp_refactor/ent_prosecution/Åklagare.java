package org.example.crimearchive.Entity.Åklagare;


import jakarta.persistence.*;

@Entity
@Table(name = "Åklagare")
public class Åklagare {

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
