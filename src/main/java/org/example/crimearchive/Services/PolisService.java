package org.example.crimearchive.services;

import org.example.crimearchive.dto.police.DTOCreatePolis;
import org.example.crimearchive.dto.police.DTOPolis;
import org.example.crimearchive.dto.police.DTOUpdatePolis;
import org.example.crimearchive.entities.police.Aina;
import org.example.crimearchive.repositories.PolisRepository;
import org.example.crimearchive.Exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PolisService {

    private final PolisRepository polisRepository;

    public PolisService(PolisRepository polisRepository) {
        this.polisRepository = polisRepository;
    }

    public List<DTOPolis> findAll() {
        return polisRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DTOPolis findById(Long id) {
        return polisRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Polis", id));
    }

    public DTOPolis save(DTOCreatePolis dto) {
        Aina polis = new Aina();
        polis.setName(dto.name());
        polis.setBadgeNumber(dto.badgeNumber());
        polis.setEmail(dto.email());
        polis.setPhone(dto.phone());
        Aina saved = polisRepository.save(polis);
        return toDTO(saved);
    }

    public DTOPolis update(Long id, DTOUpdatePolis dto) {
        Aina polis = polisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Polis", id));
        
        if (dto.name() != null) polis.setName(dto.name());
        if (dto.badgeNumber() != null) polis.setBadgeNumber(dto.badgeNumber());
        if (dto.email() != null) polis.setEmail(dto.email());
        if (dto.phone() != null) polis.setPhone(dto.phone());
        
        Aina updated = polisRepository.save(polis);
        return toDTO(updated);
    }

    public void delete(Long id) {
        if (!polisRepository.existsById(id)) {
            throw new ResourceNotFoundException("Polis", id);
        }
        polisRepository.deleteById(id);
    }

    private DTOPolis toDTO(Aina polis) {
        return new DTOPolis(
                polis.getId(),
                polis.getName(),
                polis.getBadgeNumber(),
                polis.getEmail(),
                polis.getPhone()
        );
    }
}
