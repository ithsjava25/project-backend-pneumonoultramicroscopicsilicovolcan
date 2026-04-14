package org.example.crimearchive.Controllers;

import jakarta.validation.Valid;
import org.example.crimearchive.dto.police.DTOCreatePolis;
import org.example.crimearchive.dto.police.DTOPolis;
import org.example.crimearchive.dto.police.DTOUpdatePolis;
import org.example.crimearchive.services.PolisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/police")
public class PolisController {

    private final PolisService polisService;

    public PolisController(PolisService polisService) {
        this.polisService = polisService;
    }

    @GetMapping
    public List<DTOPolis> getAllPolice() {
        return polisService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DTOPolis> getPoliceById(@PathVariable Long id) {
        return ResponseEntity.ok(polisService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DTOPolis createPolice(@Valid @RequestBody DTOCreatePolis dto) {
        return polisService.save(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DTOPolis> updatePolice(@PathVariable Long id, @RequestBody DTOUpdatePolis dto) {
        return ResponseEntity.ok(polisService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePolice(@PathVariable Long id) {
        polisService.delete(id);
    }
}
