package org.example.crimearchive.polis;

import org.example.crimearchive.DTO.Polis.DTOCreatePolis;
import org.example.crimearchive.mapper.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public List<Account> getAllAccounts() {
        return userRepository.findAll();
    }

    public void saveNewAccount(DTOCreatePolis newUser) {
        Account newAcc = Mapper.newAccountEntity(newUser, encoder.encode(newUser.password()));
        userRepository.save(newAcc);
    }
}
