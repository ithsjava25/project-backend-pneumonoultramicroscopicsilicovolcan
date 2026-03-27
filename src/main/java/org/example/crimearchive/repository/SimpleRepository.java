package org.example.crimearchive.repository;

import org.example.crimearchive.bevis.Report;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface SimpleRepository extends ListCrudRepository<Report, UUID> {
}
