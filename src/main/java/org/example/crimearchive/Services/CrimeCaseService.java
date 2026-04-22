package org.example.crimearchive.services;

import org.example.crimearchive.dto.knumber.DTOCreateKnumber;
import org.example.crimearchive.dto.knumber.DTOKnumber;
import org.example.crimearchive.dto.knumber.DTOUpdateKnumber;
import org.example.crimearchive.entities.cases.CrimeCase;
import org.example.crimearchive.repositories.CrimeCaseRepository;
import org.example.crimearchive.mappers.CrimeCaseMapper;
import org.example.crimearchive.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrimeCaseService {

    private final CrimeCaseRepository crimeCaseRepository;
    private final CrimeCaseMapper crimeCaseMapper;

    public CrimeCaseService(CrimeCaseRepository crimeCaseRepository, CrimeCaseMapper crimeCaseMapper) {
        this.crimeCaseRepository = crimeCaseRepository;
        this.crimeCaseMapper = crimeCaseMapper;
    }

    public List<DTOKnumber> findAll() {
        return crimeCaseRepository.findAll().stream()
                .map(crimeCaseMapper::toDTO)
                .collect(Collectors.toList());
    }

    public DTOKnumber findById(Long id) {
        CrimeCase crimeCase = crimeCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case", id));
        return crimeCaseMapper.toDTO(crimeCase);
    }

    public DTOKnumber findByCaseNumber(String caseNumber) {
        CrimeCase crimeCase = crimeCaseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Case with number", caseNumber));
        return crimeCaseMapper.toDTO(crimeCase);
    }

    public DTOKnumber save(DTOCreateKnumber dto) {
        CrimeCase crimeCase = new CrimeCase();
        crimeCase.setDescription(dto.description());
        CrimeCase saved = crimeCaseRepository.save(crimeCase);
        return crimeCaseMapper.toDTO(saved);
    }

    public DTOKnumber update(Long id, DTOUpdateKnumber dto) {
        CrimeCase crimeCase = crimeCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case", id));
        if (dto.description() != null) {
            crimeCase.setDescription(dto.description());
        }
        CrimeCase updated = crimeCaseRepository.save(crimeCase);
        return crimeCaseMapper.toDTO(updated);
    }

    public void delete(Long id) {
        if (!crimeCaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Case", id);
        }
        crimeCaseRepository.deleteById(id);
    }
}
