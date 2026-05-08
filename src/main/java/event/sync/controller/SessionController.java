package event.sync.controller;

import event.sync.dto.session.SessionCreateRequest;
import event.sync.service.EventService;
import event.sync.service.SessionService;
import event.sync.validator.SessionCreateValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/events/{eventId}/sessions")
@AllArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final EventService eventService;
    private final SessionCreateValidator sessionCreateValidator;

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getEventSessions(@PathVariable UUID eventId,
                                              @PathVariable UUID sessionId) {
        try {
            eventService.findById(eventId);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(sessionService.findById(sessionId));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(e.getReason());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSessions(@PathVariable UUID eventId) {
        try {
            eventService.findById(eventId);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(sessionService.getAllSessions(eventId));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(e.getReason());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }

    // TODO: Make these next routes require auth
    @PostMapping
    public ResponseEntity<?> createSession(@PathVariable UUID eventId,
                                           @RequestBody SessionCreateRequest session) {
        try {
            sessionCreateValidator.validate(session);
            eventService.findById(eventId);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(sessionService.createSession(eventId, session));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(e.getReason());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<?> updateSession(@PathVariable UUID eventId,
                                           @PathVariable UUID  sessionId,
                                           @RequestBody SessionCreateRequest session) {
        try {
            sessionCreateValidator.validate(session);
            eventService.findById(eventId);
            sessionService.findById(sessionId);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(sessionService.updateSession(eventId, session));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(e.getReason());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }
}
