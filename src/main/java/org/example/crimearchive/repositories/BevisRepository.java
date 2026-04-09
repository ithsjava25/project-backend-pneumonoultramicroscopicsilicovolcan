package org.example.crimearchive.repositories;

import org.example.crimearchive.entities.bevis.Bevis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BevisRepository extends JpaRepository<Bevis, Long> {
}
