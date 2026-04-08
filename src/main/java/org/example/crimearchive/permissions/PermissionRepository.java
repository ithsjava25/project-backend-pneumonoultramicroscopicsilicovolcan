package org.example.crimearchive.permissions;

import org.example.crimearchive.bevis.Investigation;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends ListCrudRepository<Investigation, String> {

    List<String> findAllBy(Long accoundId);

}
