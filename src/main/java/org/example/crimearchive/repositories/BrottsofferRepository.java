package org.example.crimearchive.repositories;

import org.example.crimearchive.entities.brottsoffer.Brottsoffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrottsofferRepository extends JpaRepository<Brottsoffer, Long> {
}
