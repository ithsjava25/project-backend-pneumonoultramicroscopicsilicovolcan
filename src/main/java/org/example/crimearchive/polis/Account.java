package org.example.crimearchive.polis;

import jakarta.persistence.*;
import org.example.crimearchive.audit.Auditable;
import org.example.crimearchive.cases.Cases;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Account extends Auditable implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    @ManyToMany(mappedBy = "accounts")
    private Set<Cases> permittedCases = new HashSet<>();
    private String fullName;
    private String profession;
    private String department;


    public Long getId() {
        return id;
    }

    public Account() {
        this.username = "";
    }

    public Account(String username, String password, List<String> roles,
                   String fullname, String profession, String department) {
        this.username = username;
        this.password = password;
        this.authorities = setAuthoritesList(roles);
        this.fullName = fullname;
        this.profession = profession;
        this.department = department;
    }

    public Account(String username, String password, List<String> roles) {
        this(username, password, roles, null, null, null);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getAuthoritesAsStringList(){
        return authorities.stream().map(ga ->
                ga.toString().substring(5)).toList();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> roles) {
        this.authorities = setAuthoritesList(roles);
    }

    public List<SimpleGrantedAuthority> setAuthoritesList(List<String> roles) {
        return roles.stream().map(
                        r -> r.startsWith("ROLE_") ? new SimpleGrantedAuthority(r.toUpperCase()) : new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                .toList();
    }

    public Set<Cases> getPermittedCases() {
        return permittedCases;
    }

    public void setPermittedCases(Set<Cases> permittedCases) {
        this.permittedCases = permittedCases;
    }
}
