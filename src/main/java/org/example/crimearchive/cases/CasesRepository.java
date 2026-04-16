package org.example.crimearchive.cases;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CasesRepository extends ListCrudRepository<Cases, Long> {

    Optional<Cases> findTopByOrderByCaseNumberDesc();

    Optional<Cases> findFirstByCaseNumber(String caseNumber);

    boolean existsByCaseNumber(String casenumber);

    @Query(value = "select c.id from cases c where c.case_number like concat('%', :caseNumber)", nativeQuery = true)
    Long findIdByCaseNumberContaining(String caseNumber);

    String findCaseNumberById(Long id);

    boolean existsByCaseNumberAndAccounts_Id(String caseNumber, Long accountId);


    List<Cases> findByAccountsId(Long accountId);
}
