package event.sync.controller;

import event.sync.dto.event.EventCreateRequest;
import event.sync.model.Event;
import event.sync.service.EventService;
import event.sync.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final EventService eventService;
    private final JwtService jwtService;


    public EventController(EventService eventService, JwtService jwtService) {
        this.eventService = eventService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        return ResponseEntity.ok(eventService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Event> create(
            @RequestBody EventCreateRequest request,
            @RequestHeader("Authorization") String token
    ) {
        Claims claims = jwtService.decodeToken(token);
        String id = claims.getSubject();

        return ResponseEntity.status(HttpStatus.CREATED).
                body(eventService.create(request, UUID.fromString(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> update(
            @PathVariable UUID id,
            @RequestBody EventCreateRequest request
    ) {
        return ResponseEntity.ok(eventService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}