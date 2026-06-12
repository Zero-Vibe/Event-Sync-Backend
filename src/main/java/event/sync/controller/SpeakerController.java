package event.sync.controller;

import event.sync.dto.speaker.SpeakerCreateRequest;
import event.sync.exception.NotFoundException;
import event.sync.model.Speaker;
import event.sync.service.AuthService;
import event.sync.service.JwtService;
import event.sync.service.SpeakerService;
import event.sync.validator.SpeakerCreateValidator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Controller
@RequestMapping("/speakers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SpeakerController {

    private final SpeakerService speakerService;
    private final SpeakerCreateValidator speakerCreateValidator;
    private final JwtService jwtService;
    private final AuthService authService;

    @GetMapping("/{speakerId}")
    public ResponseEntity<?> findById(@PathVariable UUID speakerId) throws NotFoundException {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(speakerService.findById(speakerId));
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
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(speakerService.getAll());
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
    public ResponseEntity<?> save(@RequestBody SpeakerCreateRequest speaker,
                                  @RequestHeader(value = "Authorization", required = false) String token
    ) {
        try {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            // speakerCreateValidator.validate(speaker);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(speakerService.create(speaker));
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

    @PutMapping("/{speakerId}")
    public ResponseEntity<?> update(@PathVariable  UUID speakerId,
                                    @RequestBody SpeakerCreateRequest speaker,
                                    @RequestHeader(value = "Authorization", required = false) String token
                                    ) throws NotFoundException {
        try {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            // speakerCreateValidator.validate(speaker);
            speakerService.findById(speakerId);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(speakerService.update(speakerId, speaker));
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

    @DeleteMapping("/{speakerId}")
    public ResponseEntity<?> delete(@PathVariable  UUID speakerId,
                                    @RequestHeader(value = "Authorization", required = false) String token
                                    ) throws NotFoundException {
        try {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            speakerService.findById(speakerId);
            speakerService.delete(speakerId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .build();
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