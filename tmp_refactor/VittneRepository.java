package org.example.crimearchive.Repository;

import org.example.crimearchive.Entity.Vittne.Vittne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VittneRepository extends JpaRepository<Vittne, Long> {
}
