package org.example.crimearchive.permissions;

import org.example.crimearchive.bevis.Cases;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends ListCrudRepository<Cases, Long> {

    List<String> findAllBy(Long accountId);

    Optional<Cases> findTopByOrderByCaseNumberDesc();

    Optional<Cases> findFirstByCaseNumber(String caseNumber);

    boolean existsByCaseNumber(String casenumber);


    List<Cases> findByAccountsId(Long accountId);
}