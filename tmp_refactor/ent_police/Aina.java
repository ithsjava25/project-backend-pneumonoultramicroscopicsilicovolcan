package org.example.crimearchive.polis;

import jakarta.persistence.*;

@Entity
@Table(name = "aina")
public class Aina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String badgeNumber;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    public Aina() {}

    public Aina(String name, String badgeNumber, String email, String phone) {
        this.name = name;
        this.badgeNumber = badgeNumber;
        this.email = email;
        this.phone = phone;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBadgeNumber() { return badgeNumber; }
    public void setBadgeNumber(String badgeNumber) { this.badgeNumber = badgeNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
