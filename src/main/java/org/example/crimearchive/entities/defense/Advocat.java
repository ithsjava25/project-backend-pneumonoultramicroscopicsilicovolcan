package org.example.crimearchive.entities.defense;


import jakarta.persistence.*;


@Entity
@Table(name = "advokat")
public class Advocat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, name = "Company")
    private String company;

    //Getters and Setters
    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }
    public String getName() {
        return name; }
    public void setName(String name) {
        this.name = name; }
    public String getTelephone() {
        return telephone; }
    public void setTelephone(String telephone) {
        this.telephone = telephone; }
    public String getEmail() {
        return email; }
    public void setEmail(String email) {
        this.email = email; }
    public String getCompany() {
        return company; }
    public void setCompany(String company) {
        this.company = company; }
}
