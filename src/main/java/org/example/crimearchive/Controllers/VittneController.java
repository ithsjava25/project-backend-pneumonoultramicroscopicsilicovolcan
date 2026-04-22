package org.example.crimearchive.controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.dto.witness.DTOCreateVittne;
import org.example.crimearchive.dto.witness.DTOUpdateVittne;
import org.example.crimearchive.dto.witness.DTOVittne;
import org.example.crimearchive.services.VittneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vittnen")
public class VittneController {

    private final VittneService vittneService;

    public VittneController(VittneService vittneService) {
        this.vittneService = vittneService;
    }

    @GetMapping
    public List<DTOVittne> getAllVittnen() {
        return vittneService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOVittne> getVittneById(@PathVariable Long id) {
        return ResponseEntity.ok(vittneService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DTOVittne createVittne(@Valid @RequestBody DTOCreateVittne dto) {
        return vittneService.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DTOVittne> updateVittne(@PathVariable Long id, @Valid @RequestBody DTOUpdateVittne dto) {
        return ResponseEntity.ok(vittneService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVittne(@PathVariable Long id) {
        vittneService.delete(id);
    }
}
