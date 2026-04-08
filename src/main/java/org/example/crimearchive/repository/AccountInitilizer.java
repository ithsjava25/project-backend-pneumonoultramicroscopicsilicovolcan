package org.example.crimearchive.repository;

import org.example.crimearchive.polis.Account;
import org.example.crimearchive.polis.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AccountInitilizer implements CommandLineRunner {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public AccountInitilizer(UserRepository repo, PasswordEncoder encoder){
        userRepository = repo;
        passwordEncoder = encoder;
    }
    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count() == 0){
            userRepository.save(createAccount("admin", "password", List.of("user", "admin")));
            userRepository.save(createAccount("demouser", "password", List.of("user")));
            userRepository.save(createAccount("officer", "password", List.of("user")));
        }
    }
    private Account createAccount(String username, String rawPassword, List<String> roles){
        return new Account(username, passwordEncoder.encode(rawPassword), roles);
    }

}
