package org.example.crimearchive.Services;

import org.example.crimearchive.DTO.Vittne.DTOCreateVittne;
import org.example.crimearchive.DTO.Vittne.DTOUpdateVittne;
import org.example.crimearchive.DTO.Vittne.DTOVittne;
import org.example.crimearchive.Entity.Vittne.Vittne;
import org.example.crimearchive.Repository.VittneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VittneService {
    private final VittneRepository vittneRepository;

    public VittneService(VittneRepository vittneRepository) {
        this.vittneRepository = vittneRepository;
    }

    public List<DTOVittne> findAll() {
        return vittneRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DTOVittne findById(Long id) {
        Vittne vittne = vittneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vittne not found"));
        return mapToDTO(vittne);
    }

    public DTOVittne save(DTOCreateVittne dto) {
        Vittne vittne = new Vittne();
        vittne.setName(dto.name());
        vittne.setEmail(dto.email());
        vittne.setPhone(dto.phone());
        Vittne saved = vittneRepository.save(vittne);
        return mapToDTO(saved);
    }

    public DTOVittne update(Long id, DTOUpdateVittne dto) {
        Vittne vittne = vittneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vittne not found"));
        vittne.setName(dto.name());
        vittne.setEmail(dto.email());
        vittne.setPhone(dto.phone());
        Vittne updated = vittneRepository.save(vittne);
        return mapToDTO(updated);
    }

    public void delete(Long id) {
        vittneRepository.deleteById(id);
    }

    private DTOVittne mapToDTO(Vittne vittne) {
        return new DTOVittne(
                vittne.getId(),
                vittne.getName(),
                vittne.getEmail(),
                vittne.getPhone()
        );
    }
}
