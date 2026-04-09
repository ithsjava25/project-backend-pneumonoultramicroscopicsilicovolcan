package org.example.crimearchive.cases;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CasesRepository extends ListCrudRepository<Cases, Long> {

    List<String> findAllBy(Long accountId);

    Optional<Cases> findTopByOrderByCaseNumberDesc();

    Optional<Cases> findFirstByCaseNumber(String caseNumber);

    boolean existsByCaseNumber(String casenumber);



    List<Cases> findByAccountsId(Long accountId);
}