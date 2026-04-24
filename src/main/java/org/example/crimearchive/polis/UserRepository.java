package org.example.crimearchive.polis;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface UserRepository extends ListCrudRepository<Account, Long> {
    Account findUserByUsername(String username);

    List<Account> findAllByIdNot(Long id);

    boolean existsByUsername(String username);
}
