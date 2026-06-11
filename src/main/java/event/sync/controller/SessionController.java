package event.sync.controller;

import event.sync.dto.session.SessionCreateRequest;
import event.sync.exception.NotFoundException;
import event.sync.model.Event;
import event.sync.model.Session;
import event.sync.service.AuthService;
import event.sync.service.EventService;
import event.sync.service.JwtService;
import event.sync.service.SessionService;
import event.sync.validator.SessionCreateValidator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events/{eventId}/sessions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SessionController {

    private final SessionService sessionService;
    private final EventService eventService;
    private final SessionCreateValidator sessionCreateValidator;
    private final JwtService jwtService;
    private final AuthService authService;

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getEventSessions(@PathVariable UUID eventId,
                                              @PathVariable UUID sessionId) throws NotFoundException {
        try {
            isEventRelated(eventId, sessionId);
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
    public ResponseEntity<?> getAllSessions(@PathVariable UUID eventId,
                                            @RequestParam(required = false, value = "_start", defaultValue = "0") Integer start,
                                            @RequestParam(required = false, value = "_end", defaultValue = "10") Integer end,
                                            @RequestParam(required = false, value = "_sort", defaultValue = "start_time") String sort,
                                            @RequestParam(required = false, value = "_order", defaultValue = "ASC") String order) throws NotFoundException {
        try {
            eventService.findById(eventId);

            int pageSize = end - start;
            int pageNumber = start / pageSize;

            Page<Session> pagedResult = sessionService.getAll(pageNumber, pageSize, sort, order, eventId);

            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(pagedResult);
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

    @PostMapping
    public ResponseEntity<?> createSession(@PathVariable UUID eventId,
                                           @RequestBody SessionCreateRequest session,
                                           @RequestHeader(value = "Authorization", required = false) String token
                                           ) throws NotFoundException {
        try {
            if (token == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            // sessionCreateValidator.validate(session);
            eventService.findById(eventId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(sessionService.create(session));
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
                                           @RequestBody SessionCreateRequest session,
                                           @RequestHeader(value = "Authorization", required = false) String token
                                            ) throws NotFoundException {
        try {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            // sessionCreateValidator.validate(session);
            isEventRelated(eventId, sessionId);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(sessionService.update(sessionId, session));
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

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> deleteSession(@PathVariable UUID eventId,
                                           @PathVariable UUID  sessionId,
                                           @RequestHeader(value = "Authorization", required = false) String token
                                            ) throws NotFoundException {
        try {
            if (token == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            isEventRelated(eventId, sessionId);
            sessionService.delete(sessionId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(e.getReason());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getStackTrace());
        }
    }

    private void isEventRelated(UUID eventId, UUID sessionId) throws NotFoundException {
        Event event = eventService.findById(eventId);
        if (event.getSessions().stream()
                .noneMatch(s -> s.getId().equals(sessionId))) {
            throw new NotFoundException("Session not found in event: " + event.getTitle());
        }
    };
}
