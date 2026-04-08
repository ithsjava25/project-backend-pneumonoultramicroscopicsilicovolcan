package org.example.crimearchive.polis;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
public class Account implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
//    @ManyToMany()
//    @JoinTable(
//            name = "account_cases",
//            joinColumns = @JoinColumn(name = "account_id"),
//            inverseJoinColumns = @JoinColumn(name = "case_number")
//    )
//    private Set<Investigation> permittedCases;


    public Long getId() {
        return id;
    }

    public Account() {
        this.username = "";
    }

    public Account(String username, String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.authorities = setAuthoritesList(roles);
    }

//    public Set<Report> getDocumentAccess() {
//        return documentAccess;
//    }
//
//    public void setDocumentAccess(Set<Report> documentAccess) {
//        this.documentAccess = documentAccess;
//    }

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
}
