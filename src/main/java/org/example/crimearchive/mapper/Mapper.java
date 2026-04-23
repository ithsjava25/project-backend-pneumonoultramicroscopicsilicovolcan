package org.example.crimearchive.mapper;

import org.example.crimearchive.dto.CreateReport;
import org.example.crimearchive.dto.Polis.DTOCreatePolis;
import org.example.crimearchive.dto.Polis.DTOUpdatePolis;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.reports.Report;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Mapper {

    public static Report toEntity(CreateReport report, String s3KeyPdf, String s3KeyFile) {
        return new Report(
                UUID.randomUUID(),
                report.name(),
                report.event()

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
                "",
                convertGAtoStringList(updateAccount.getAuthorities())
        );
    }

    private static String convertGAtoStringList(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(
                r -> r.toString().startsWith("ROLE_") ? r.toString().substring(5) : r.toString()).collect(Collectors.joining(",")).toString();
    }

}
