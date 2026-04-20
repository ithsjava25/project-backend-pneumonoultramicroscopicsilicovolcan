package org.example.crimearchive.reports;

import org.example.crimearchive.cases.Cases;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface ReportRepository extends ListCrudRepository<Report, UUID> {

//    @Query(value = "select case_number from report where case_number like CONCAT('%', :year, '%') order by case_number desc limit 1",
//            nativeQuery = true)
//    String findLastKnumber(@Param("year") String year);
//
//    boolean existsByCaseNumber(String caseNumber);

    long count();

    List<Report> findAllByCaseEntity(Cases caseEntity);

    Report getReportByCaseEntity(Cases caseEntity);

}
