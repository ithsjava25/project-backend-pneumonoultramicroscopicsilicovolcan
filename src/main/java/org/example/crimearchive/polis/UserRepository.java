package org.example.crimearchive.polis;

import org.springframework.data.repository.ListCrudRepository;

public interface UserRepository extends ListCrudRepository<Account, Long> {
    Account findUserByUsername(String username);
}
