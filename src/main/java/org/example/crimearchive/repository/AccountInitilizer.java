package org.example.crimearchive.repository;

import org.example.crimearchive.cases.CaseService;
import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountInitilizer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CaseService caseService;
    private PasswordEncoder passwordEncoder;

    public AccountInitilizer(UserRepository repo, CaseService caseService, PasswordEncoder encoder) {
        userRepository = repo;
        passwordEncoder = encoder;
        this.caseService = caseService;
    }
    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count() == 0){
            userRepository.save(createAccount("admin", "password", List.of("user", "admin")));
            userRepository.save(createAccount("demouser", "password", List.of("user")));
            userRepository.save(createAccount("officer", "password", List.of("user")));

            caseService.addAccountToCase(1L, "K-2026-000001");
            caseService.addAccountToCase(1L, "K-2026-000002");
            caseService.addAccountToCase(1L, "K-2026-000003");
        }
    }
    private Account createAccount(String username, String rawPassword, List<String> roles){
        return new Account(username, passwordEncoder.encode(rawPassword), roles);
    }

}
