package org.example.crimearchive.polis;

import org.example.crimearchive.DTO.Polis.DTOCreatePolis;
import org.example.crimearchive.DTO.Polis.DTOUpdatePolis;
import org.example.crimearchive.DTO.Polis.DTOUpdateProfile;
import org.example.crimearchive.exceptions.PasswordValidationException;
import org.example.crimearchive.mapper.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private PasswordEncoder encoder;
    private final Set<String> VALID_ROLES = Set.of("USER", "HANDLER", "ADMIN");
    private final int MINIMUM_PASSWORD_LENGTH = 5;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public List<Account> getAllAccounts() {
        return userRepository.findAll();
    }

    public List<Account> getAllAccountsButloggedin(Account loggedIn){
        return userRepository.findAllByIdNot(loggedIn.getId());
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
        Account dbAccount = userRepository.findById(accId).orElseThrow(() -> new RuntimeException("Inget konto funnet"));

        dbAccount.setFullName(updatedAcc.fullName());
        dbAccount.setUsername(updatedAcc.username());
        dbAccount.setDepartment(updatedAcc.department());
        dbAccount.setProfession(updatedAcc.profession());
        dbAccount.setAuthorities(convertStringToListString(updatedAcc.roles()));

        if (updatedAcc.password() != null && !updatedAcc.password().isBlank()) {
            if(updatedAcc.password().length() <= MINIMUM_PASSWORD_LENGTH) throw new PasswordValidationException("Lösenordet måste vara längre än 5");
            dbAccount.setPassword(encoder.encode(updatedAcc.password()));
        }
    }

    public DTOUpdateProfile prefillProfileFields(Account user){
        return new DTOUpdateProfile(user.getFullName(), "", user.getId());
    }

    @Transactional
    public void updateProfile(DTOUpdateProfile profileUpdate){
        Account updateProfile = userRepository.findById(profileUpdate.id())
                .orElseThrow(() -> new RuntimeException("Kontot hittades inte"));
        updateProfile.setFullName(profileUpdate.fullname());
        if(profileUpdate.password() != null && !profileUpdate.password().isBlank()){
            if(profileUpdate.password().length() <= MINIMUM_PASSWORD_LENGTH) throw new PasswordValidationException("Lösenordet måste vara längre än 5");
            updateProfile.setPassword(encoder.encode(profileUpdate.password().trim()));
        }
    }

    private List<String> convertStringToListString(String roles) {
        if (roles == null || roles.isBlank()) return List.of();

        List<String> updateList = Arrays.stream(roles.toUpperCase()
                .split(",")).map(String::trim).distinct().toList();
        if (!VALID_ROLES.containsAll(updateList)) throw new IllegalArgumentException("Felaktiga roller insatta");
        return updateList;
    }
}
