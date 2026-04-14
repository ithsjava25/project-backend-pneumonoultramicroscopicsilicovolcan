package org.example.crimearchive.repositories;

import org.example.crimearchive.entities.prosecution.Åklagare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ÅklagareRepository extends JpaRepository<Åklagare, Long> {
}
