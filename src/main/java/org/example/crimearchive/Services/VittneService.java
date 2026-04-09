package org.example.crimearchive.services;

import org.example.crimearchive.dto.witness.DTOCreateVittne;
import org.example.crimearchive.dto.witness.DTOUpdateVittne;
import org.example.crimearchive.dto.witness.DTOVittne;
import org.example.crimearchive.entities.Vittne.Vittne;
import org.example.crimearchive.Exceptions.ResourceNotFoundException;
import org.example.crimearchive.mappers.VittneMapper;
import org.example.crimearchive.repositories.VittneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VittneService {
    private final VittneRepository vittneRepository;
    private final VittneMapper vittneMapper;

    public VittneService(VittneRepository vittneRepository, VittneMapper vittneMapper) {
        this.vittneRepository = vittneRepository;
        this.vittneMapper = vittneMapper;
    }

    public List<DTOVittne> findAll() {
        return vittneRepository.findAll().stream()
                .map(vittneMapper::toDTO)
                .collect(Collectors.toList());
    }

    public DTOVittne findById(Long id) {
        Vittne vittne = vittneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vittne", id));
        return vittneMapper.toDTO(vittne);
    }

    public DTOVittne save(DTOCreateVittne dto) {
        Vittne vittne = vittneMapper.toEntity(dto);
        Vittne saved = vittneRepository.save(vittne);
        return vittneMapper.toDTO(saved);
    }

    public DTOVittne update(Long id, DTOUpdateVittne dto) {
        Vittne vittne = vittneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vittne", id));
        vittneMapper.updateEntity(vittne, dto);
        Vittne updated = vittneRepository.save(vittne);
        return vittneMapper.toDTO(updated);
    }

    public void delete(Long id) {
        if (!vittneRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vittne", id);
        }
        vittneRepository.deleteById(id);
    }
}
