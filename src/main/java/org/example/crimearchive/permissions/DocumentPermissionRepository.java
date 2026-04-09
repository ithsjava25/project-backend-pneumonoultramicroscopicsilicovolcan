package org.example.crimearchive.permissions;

import org.example.crimearchive.reports.Report;
import org.springframework.data.repository.ListCrudRepository;

public interface DocumentPermissionRepository extends ListCrudRepository<Report, Long> {
}
