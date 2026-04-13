package org.example.crimearchive.Controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.dto.knumber.DTOCreateKnumber;
import org.example.crimearchive.dto.knumber.DTOKnumber;
import org.example.crimearchive.dto.knumber.DTOUpdateKnumber;
import org.example.crimearchive.services.CrimeCaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
public class CrimeCaseController {

    private final CrimeCaseService crimeCaseService;

    public CrimeCaseController(CrimeCaseService crimeCaseService) {
        this.crimeCaseService = crimeCaseService;
    }

    @GetMapping
    public List<DTOKnumber> getAllCases() {
        return crimeCaseService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOKnumber> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(crimeCaseService.findById(id));
    }

    @GetMapping("/number/{caseNumber}")
    public ResponseEntity<DTOKnumber> getCaseByNumber(@PathVariable String caseNumber) {
        return ResponseEntity.ok(crimeCaseService.findByCaseNumber(caseNumber));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DTOKnumber createCase(@Valid @RequestBody DTOCreateKnumber dto) {
        return crimeCaseService.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DTOKnumber> updateCase(@PathVariable Long id, @RequestBody DTOUpdateKnumber dto) {
        return ResponseEntity.ok(crimeCaseService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCase(@PathVariable Long id) {
        crimeCaseService.delete(id);
    }
}
