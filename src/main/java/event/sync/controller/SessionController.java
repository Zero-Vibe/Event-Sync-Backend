package event.sync.controller;

import event.sync.dto.session.SessionCreateRequest;
import event.sync.exception.BadRequestException;
import event.sync.exception.ConflictException;
import event.sync.exception.NotFoundException;
import event.sync.model.Event;
import event.sync.model.Session;
import event.sync.model.User;
import event.sync.service.*;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
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
        isEventRelated(
                eventService.findById(eventId),
                sessionService.findById(sessionId)
        );

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(sessionService.findById(sessionId));
    }

    @GetMapping
    public ResponseEntity<?> getAllSessions(@PathVariable UUID eventId,
                                            @RequestParam(required = false, value = "_start", defaultValue = "0") Integer start,
                                            @RequestParam(required = false, value = "_end", defaultValue = "10") Integer end,
                                            @RequestParam(required = false, value = "_sort", defaultValue = "startTime") String sort,
                                            @RequestParam(required = false, value = "_order", defaultValue = "ASC") String order) throws NotFoundException {
        eventService.findById(eventId);

        int pageSize = end - start;
        int pageNumber = start / pageSize;

        Page<Session> pagedResult = sessionService.getAll(pageNumber, pageSize, sort, order, eventId);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .header("X-Total-Count", String.valueOf(pagedResult.getTotalElements()))
                .body(pagedResult.getContent());
    }

    @PostMapping
    public ResponseEntity<?> createSession(@PathVariable UUID eventId,
                                           @RequestBody @Valid SessionCreateRequest session,
                                           @RequestHeader(value = "Authorization", required = false) String token
                                           ) throws NotFoundException, BadRequestException {
        Claims claims = jwtService.decodeToken(token);
        authService.checkIfAdmin(claims);

        isInEventTimeRange(eventService.findById(eventId), session);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(sessionService.create(session));
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<?> updateSession(@PathVariable UUID eventId,
                                           @PathVariable UUID  sessionId,
                                           @RequestBody @Valid SessionCreateRequest session,
                                           @RequestHeader(value = "Authorization", required = false) String token
                                            ) throws NotFoundException, BadRequestException {
        Claims claims = jwtService.decodeToken(token);
        authService.checkIfAdmin(claims);

        Event event = eventService.findById(eventId);
        isInEventTimeRange(event, session);
        isEventRelated(event, sessionService.findById(sessionId));
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(sessionService.update(sessionId, session));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<?> deleteSession(@PathVariable UUID eventId,
                                           @PathVariable UUID  sessionId,
                                           @RequestHeader(value = "Authorization", required = false) String token
                                            ) throws NotFoundException {
        Claims claims = jwtService.decodeToken(token);
        authService.checkIfAdmin(claims);

        sessionService.delete(eventId, sessionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/{sessionId}/register")
    public ResponseEntity<?> getRegisterCount(@PathVariable UUID eventId,
                                              @PathVariable UUID sessionId) throws NotFoundException {

        isEventRelated(
                eventService.findById(eventId),
                sessionService.findById(sessionId)
        );
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(sessionService.getRegisterCount(sessionId));
    }

    @PostMapping("/{sessionId}/register")
    public ResponseEntity<?> register(@PathVariable UUID eventId,
                                      @PathVariable UUID sessionId,
                                      @RequestHeader(value = "Authorization") String token) throws NotFoundException, BadRequestException, ConflictException {
        User user = userService.findById(UUID.fromString(jwtService.decodeToken(token).getSubject()));
        Session session = sessionService.findById(sessionId);

        isEventRelated(eventService.findById(eventId), session);
        if (session.getStartTime().isBefore(Instant.now())) {
            throw new BadRequestException("Session has already been started");
        }

        sessionService.register(session, user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{sessionId}/register")
    public ResponseEntity<?> unregister(@PathVariable UUID eventId,
                                        @PathVariable UUID sessionId,
                                        @RequestHeader(value = "Authorization") String token) throws NotFoundException, BadRequestException {
        UUID userId = UUID.fromString(jwtService.decodeToken(token).getSubject());
        userService.findById(userId);
        Session session = sessionService.findById(sessionId);

        isEventRelated(eventService.findById(eventId), session);
        if (session.getStartTime().isBefore(Instant.now())) {
            throw new BadRequestException("Session has already been started");
        }

        sessionService.unregister(sessionId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private void isEventRelated(Event event, Session session) throws NotFoundException {
        if (event.getSessions().stream()
                .noneMatch(s -> s.getId().equals(session.getId()))) {
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
}
