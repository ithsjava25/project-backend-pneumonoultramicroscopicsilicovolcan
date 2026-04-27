package org.example.crimearchive.polis;

import jakarta.persistence.*;
import org.example.crimearchive.audit.Auditable;
import org.example.crimearchive.contactInformation.Clearance;
import org.example.crimearchive.contactInformation.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
public class Aina extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private final String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Clearance department;

    public Aina(String username, String password,
                String email, List<String> roles){
        this.username = username;
        this.password = password;
        //this.email = new Email(email);
        this.authorities = roles.stream()
                .map(
                        r -> r.startsWith("ROLE_") ? new SimpleGrantedAuthority(r) : new SimpleGrantedAuthority("ROLE_" + r))
                .toList();
    }
    public Aina(){
        this.username = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public Clearance getDepartment() {
        return department;
    }

    public void setDepartment(Clearance department) {
        this.department = department;
    }
}
