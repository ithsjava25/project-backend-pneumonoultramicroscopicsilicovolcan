package org.example.crimearchive.polis;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AccountUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public AccountUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public Account loadUserByUsername(String username) throws UsernameNotFoundException{
        Account account = userRepository.findUserByUsername(username);
        if (account == null)
            throw new UsernameNotFoundException("Ingen användare funnen");

        var authorites = account.getAuthorities();
        return account;
    }


}
