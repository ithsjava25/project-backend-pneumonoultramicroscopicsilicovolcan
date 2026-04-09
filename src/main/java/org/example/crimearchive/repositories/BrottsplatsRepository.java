package org.example.crimearchive.repositories;

import org.example.crimearchive.entities.bevis.Brottsplats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrottsplatsRepository extends JpaRepository<Brottsplats, Long> {
}
