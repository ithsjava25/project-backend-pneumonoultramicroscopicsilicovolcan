package org.example.crimearchive.mapper;

import org.example.crimearchive.DTO.CreateReport;
import org.example.crimearchive.DTO.Polis.DTOCreatePolis;
import org.example.crimearchive.DTO.Polis.DTOUpdatePolis;
import org.example.crimearchive.DTO.Polis.UpdatePolice;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.reports.Report;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Mapper {

    public static Report toEntity(CreateReport report, String s3KeyPdf, String s3KeyFile) {
        return new Report(
                UUID.randomUUID(),
                report.name(),
                report.event(),
                s3KeyPdf,
                s3KeyFile

        );
    }

    public static Account newAccountEntity(DTOCreatePolis newUser, String encodedPassword, List<String> splitRoles) {
        return new Account(
                newUser.username(),
                encodedPassword,
                splitRoles,
                newUser.fullName(),
                newUser.profession(),
                newUser.department());
    }

    public static DTOUpdatePolis updateAccountDTO(Account updateAccount){
        return new DTOUpdatePolis(
                updateAccount.getId(),
                updateAccount.getFullName(),
                updateAccount.getProfession(),
                updateAccount.getDepartment(),
                updateAccount.getUsername(),
                //fixa så att man inte måste ändra lösenord
                "",
                //mappa om granted auth till string?
                convertGAtoStringList(updateAccount.getAuthorities())
        );
    }

    private static List<String> convertGAtoStringList(Collection<? extends GrantedAuthority> authorities){
        return authorities.stream().map(
                ga -> ga.toString()).toList();
    }

}
