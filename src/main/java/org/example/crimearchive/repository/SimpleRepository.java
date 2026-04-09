package org.example.crimearchive.repository;

import org.example.crimearchive.bevis.Report;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface SimpleRepository extends ListCrudRepository<Report, UUID> {

//    @Query(value = "select case_number from report where case_number like CONCAT('%', :year, '%') order by case_number desc limit 1",
//            nativeQuery = true)
//    String findLastKnumber(@Param("year") String year);
//
//    boolean existsByCaseNumber(String caseNumber);

    long count();

//    List<Report> findAllByCaseNumberIn(List<String> permittedCaseNumbers);

}
