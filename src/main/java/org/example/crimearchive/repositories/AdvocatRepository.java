package org.example.crimearchive.repositories;

import org.example.crimearchive.entities.defense.Advocat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvocatRepository extends JpaRepository<Advocat, Long> {
}
