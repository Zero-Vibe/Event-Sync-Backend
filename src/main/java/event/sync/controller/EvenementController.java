package event.sync.controller;

import event.sync.dto.event.EvenementCreateRequest;
import event.sync.model.Evenement;
import event.sync.service.EvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/evenements")
@RequiredArgsConstructor
public class EvenementController {

    private final EvenementService evenementService;

    @GetMapping
    public ResponseEntity<List<Evenement>> getAll() {
        return ResponseEntity.ok(evenementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(evenementService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Evenement> create(
            @RequestBody EvenementCreateRequest request,
            @RequestHeader("X-Organisateur-Id") UUID organisateurId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(evenementService.create(request, organisateurId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evenement> update(
            @PathVariable UUID id,
            @RequestBody EvenementCreateRequest request
    ) {
        return ResponseEntity.ok(evenementService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        evenementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}