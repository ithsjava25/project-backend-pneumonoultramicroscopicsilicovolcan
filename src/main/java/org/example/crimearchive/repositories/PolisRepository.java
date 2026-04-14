package org.example.crimearchive.repositories;

import org.example.crimearchive.entities.police.Aina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolisRepository extends JpaRepository<Aina, Long> {
    Optional<Aina> findByBadgeNumber(String badgeNumber);
}
