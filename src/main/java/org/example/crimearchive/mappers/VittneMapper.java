package org.example.crimearchive.mappers;

import org.example.crimearchive.dto.witness.DTOCreateVittne;
import org.example.crimearchive.dto.witness.DTOUpdateVittne;
import org.example.crimearchive.dto.witness.DTOVittne;
import org.example.crimearchive.entities.Vittne.Vittne;
import org.springframework.stereotype.Component;

@Component
public class VittneMapper {

    public DTOVittne toDTO(Vittne vittne) {
        if (vittne == null) {
            return null;
        }
        return new DTOVittne(
                vittne.getId(),
                vittne.getName(),
                vittne.getEmail(),
                vittne.getPhone()
        );
    }

    public Vittne toEntity(DTOCreateVittne dto) {
        if (dto == null) {
            return null;
        }
        Vittne vittne = new Vittne();
        vittne.setName(dto.name());
        vittne.setEmail(dto.email());
        vittne.setPhone(dto.phone());
        return vittne;
    }

    public void updateEntity(Vittne vittne, DTOUpdateVittne dto) {
        if (dto == null || vittne == null) {
            return;
        }
        if (dto.name() != null) {
            vittne.setName(dto.name());
        }
        if (dto.email() != null) {
            vittne.setEmail(dto.email());
        }
        if (dto.phone() != null) {
            vittne.setPhone(dto.phone());
        }
    }
}
