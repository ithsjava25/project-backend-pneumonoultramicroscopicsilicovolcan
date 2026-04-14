package org.example.crimearchive.contactInformation;

public record Email(String adress, String domain) {
    public Email(String email){
        this(email.split("@")[0], email.split("@")[1]);}
    }
