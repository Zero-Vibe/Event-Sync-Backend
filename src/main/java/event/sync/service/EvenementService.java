package event.sync.service;

import event.sync.dto.event.EvenementCreateRequest;
import event.sync.model.Evenement;
import event.sync.repository.EvenementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvenementService {

    private final EvenementRepository evenementRepository;

    public List<Evenement> findAll() {
        return evenementRepository.findAll();
    }

    public Evenement findById(UUID id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + id));
    }

    public Evenement create(EvenementCreateRequest request, UUID organisateurId) {
        if (!request.getDateFin().isAfter(request.getDateDebut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }

        return evenementRepository.save(Evenement.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .lieu(request.getLieu())
                .createdBy(organisateurId)
                .build());
    }

    public Evenement update(UUID id, EvenementCreateRequest request) {
        findById(id);

        if (!request.getDateFin().isAfter(request.getDateDebut())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }

        return evenementRepository.update(id, Evenement.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .lieu(request.getLieu())
                .build())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + id));
    }

    public void delete(UUID id) {
        findById(id);
        evenementRepository.deleteById(id);
    }
}