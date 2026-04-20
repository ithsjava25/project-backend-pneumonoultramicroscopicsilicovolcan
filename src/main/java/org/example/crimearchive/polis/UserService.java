package org.example.crimearchive.polis;

import org.example.crimearchive.DTO.Polis.DTOCreatePolis;
import org.example.crimearchive.mapper.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private PasswordEncoder encoder;
    private final List<String> VALID_ROLES = List.of("USER", "HANDLER", "ADMIN");

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
}
