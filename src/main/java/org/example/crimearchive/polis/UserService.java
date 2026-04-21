package org.example.crimearchive.polis;

import org.example.crimearchive.DTO.Polis.DTOCreatePolis;
import org.example.crimearchive.DTO.Polis.DTOUpdatePolis;
import org.example.crimearchive.mapper.Mapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private PasswordEncoder encoder;
    private final Set<String> VALID_ROLES = Set.of("USER", "HANDLER", "ADMIN");

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public List<Account> getAllAccounts() {
        return userRepository.findAll();
    }

    public void saveNewAccount(DTOCreatePolis newUser) {
        List<String> assignedRoles = Arrays.stream(newUser.roles().toUpperCase().split(",")).map(String::trim).toList();
        if (!VALID_ROLES.containsAll(assignedRoles)) throw new IllegalArgumentException("Felaktiga roller");

        Account newAcc = Mapper.newAccountEntity(newUser, encoder.encode(newUser.password()), assignedRoles);
        userRepository.save(newAcc);
    }

    public DTOUpdatePolis getDTOUpdateAccountById(Long id) {
        return Mapper.updateAccountDTO(userRepository.findById(id).orElseThrow(()-> new RuntimeException("No user found")));
    }

    @Transactional
    public void updateAccount(DTOUpdatePolis updatedAcc) {
        Long accId = updatedAcc.id();
        System.out.println("ACC ID: " + accId);
        Account dbAccount = userRepository.findById(accId).orElseThrow(() -> new RuntimeException("Inget konto funnet"));

        dbAccount.setFullName(updatedAcc.fullName());
        dbAccount.setUsername(updatedAcc.username());
        dbAccount.setDepartment(updatedAcc.department());
        dbAccount.setProfession(updatedAcc.profession());
        dbAccount.setAuthorities(convertStringToListString(updatedAcc.roles()));

        if (updatedAcc.password() != null && !updatedAcc.password().isBlank()) {
            dbAccount.setPassword(encoder.encode(updatedAcc.password()));
        }
    }

    private Collection<? extends GrantedAuthority> convertRoleStringsToList(String roles) {
        if (roles == null) return Collections.emptyList();

        List<SimpleGrantedAuthority> updatedList = Arrays.stream(roles.toUpperCase().split(",")).map(
                r -> r.trim().startsWith("ROLE_") ? new SimpleGrantedAuthority(r) : new SimpleGrantedAuthority("ROLE_" + r)
        ).toList();
        if (!VALID_ROLES.containsAll(updatedList)) throw new IllegalArgumentException("Felaktiga roller insatta");
        return updatedList;
    }

    private List<String> convertStringToListString(String roles) {
        if (roles == null || roles.isBlank()) return List.of();

        List<String> updateList = Arrays.stream(roles.toUpperCase()
                .split(",")).map(String::trim).distinct().toList();
        if (!VALID_ROLES.containsAll(updateList)) throw new IllegalArgumentException("Felaktiga roller insatta");
        return updateList;
    }
}
