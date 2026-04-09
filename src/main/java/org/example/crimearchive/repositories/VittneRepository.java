package org.example.crimearchive.repositories;

import org.example.crimearchive.entities.Vittne.Vittne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VittneRepository extends JpaRepository<Vittne, Long> {
}
