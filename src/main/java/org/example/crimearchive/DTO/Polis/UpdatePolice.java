package org.example.crimearchive.DTO.Polis;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.sql.Update;

import java.util.List;

public class UpdatePolice {
            Long id;
            @NotBlank(message = "Namn får inte vara tomt")
            String fullName;
            @NotBlank(message = "Yrke får inte vara tomt")
            String profession;
            @NotBlank(message = "Avdelning får inte vara tomt")
            String department;
            @NotBlank(message = "Användarnamn får inte vara tomt")
            String username;
            @NotBlank(message = "Lösenordet får inte vara tomt")
            String password;
            List<String> roles;

            public UpdatePolice(){}

            public UpdatePolice(Long id, String fullname, String profession,
                                String department,String username, String password,
                                List<String> roles){
                this.id = id;
                this.fullName = fullname;
                this.profession = profession;
                this.department = department;
                this.username = username;
                this.password = password;
                this.roles = roles;
            }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
