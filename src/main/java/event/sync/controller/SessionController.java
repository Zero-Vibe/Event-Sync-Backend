package event.sync.controller;

import event.sync.dto.session.SessionCreateRequest;
import event.sync.exception.BadRequestException;
import event.sync.exception.NotFoundException;
import event.sync.model.Event;
import event.sync.model.Session;
import event.sync.service.*;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/events/{eventId}/sessions")
@RequiredArgsConstructor
@CrossOrigin(exposedHeaders = "X-Total-Count")
public class SessionController {

    private final SessionService sessionService;
    private final EventService eventService;
    private final JwtService jwtService;
    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> findById(@PathVariable UUID eventId,
                                      @PathVariable UUID sessionId) throws NotFoundException {
        try {
            isEventRelated(eventService.findById(eventId), sessionId);
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
                                            @RequestParam(required = false, value = "_sort", defaultValue = "startTime") String sort,
                                            @RequestParam(required = false, value = "_order", defaultValue = "ASC") String order) throws NotFoundException {
        try {
            eventService.findById(eventId);

            int pageSize = end - start;
            int pageNumber = start / pageSize;

            Page<Session> pagedResult = sessionService.getAll(pageNumber, pageSize, sort, order, eventId);

            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .header("X-Total-Count", String.valueOf(pagedResult.getTotalElements()))
                    .body(pagedResult.getContent());
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
                                           @RequestBody @Valid SessionCreateRequest session,
                                           @RequestHeader(value = "Authorization", required = false) String token
                                           ) throws NotFoundException, BadRequestException {
        try {
            if (token == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            }

            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            isInEventTimeRange(eventService.findById(eventId), session);
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
                                           @RequestBody @Valid SessionCreateRequest session,
                                           @RequestHeader(value = "Authorization", required = false) String token
                                            ) throws NotFoundException, BadRequestException {
        try {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            Event event = eventService.findById(eventId);
            isInEventTimeRange(event, session);
            isEventRelated(event, sessionId);
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

            sessionService.delete(eventId, sessionId);
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

    private void isEventRelated(Event event, UUID sessionId) throws NotFoundException {
        if (event.getSessions().stream()
                .noneMatch(s -> s.getId().equals(sessionId))) {
            throw new NotFoundException("Session not found in event: " + event.getTitle());
        }
    };

    private void isInEventTimeRange(Event event, SessionCreateRequest session) throws BadRequestException {
        if ((!event.getStartDateTime().equals(session.getStartTime()) && event.getStartDateTime().isAfter(session.getStartTime()))
            || (!event.getEndDateTime().equals(session.getEndTime()) && event.getEndDateTime().isBefore(session.getEndTime()))) {
            throw new BadRequestException("Session time range must be between within event time range: ["
                    + event.getStartDateTime() + " - " + event.getEndDateTime()
                    + "]");
        }
    }

    @GetMapping("/{sessionId}/register")
    public ResponseEntity<?> getRegisterCount(@PathVariable UUID eventId,
                                              @PathVariable UUID sessionId) throws NotFoundException, BadRequestException {

        isEventRelated(eventService.findById(eventId), sessionId);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(sessionService.getRegisterCount(sessionId));
    }

    @PostMapping("/{sessionId}/register")
    public ResponseEntity<?> register(@PathVariable UUID eventId,
                                      @PathVariable UUID sessionId,
                                      @RequestHeader(value = "Authorization") String token) throws NotFoundException, BadRequestException {
        if  (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(jwtService.decodeToken(token).getSubject());
        userService.findById(userId);
        isEventRelated(eventService.findById(eventId), sessionId);

        sessionService.register(sessionId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{sessionId}/register")
    public ResponseEntity<?> unregister(@PathVariable UUID eventId,
                                        @PathVariable UUID sessionId,
                                        @RequestHeader(value = "Authorization") String token) throws NotFoundException {
        if  (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UUID userId = UUID.fromString(jwtService.decodeToken(token).getSubject());
        userService.findById(userId);
        isEventRelated(eventService.findById(eventId), sessionId);

        sessionService.unregister(sessionId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
