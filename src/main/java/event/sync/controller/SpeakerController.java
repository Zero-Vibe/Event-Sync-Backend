package event.sync.controller;

import event.sync.dto.speaker.SpeakerCreateRequest;
import event.sync.exception.NotFoundException;
import event.sync.model.Speaker;
import event.sync.service.AuthService;
import event.sync.service.JwtService;
import event.sync.service.SpeakerService;
import event.sync.validator.SpeakerCreateValidator;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/speakers")
@RequiredArgsConstructor
@CrossOrigin(exposedHeaders = "X-Total-Count")
public class SpeakerController {

    private final SpeakerService speakerService;
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
    public ResponseEntity<?> getAll(@RequestParam(required = false, value = "_start", defaultValue = "0") Integer start,
                                    @RequestParam(required = false, value = "_end", defaultValue = "10") Integer end,
                                    @RequestParam(required = false, value = "_sort", defaultValue = "firstName") String sort,
                                    @RequestParam(required = false, value = "_order", defaultValue = "ASC") String order,
                                    @RequestParam(required = false, value = "filter", defaultValue = "{}") String filterJson,
                                    @RequestParam(required = false, value = "id") List<UUID> ids
    ) {
        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(speakerService.getMany(ids));
        }
        try {
            int pageSize = end - start;
            int pageNumber = start / pageSize;

            Page<Speaker> pagedResult = speakerService.findAll(pageNumber, pageSize, sort, order, filterJson);

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
    public ResponseEntity<?> save(@RequestBody @Valid SpeakerCreateRequest speaker,
                                  @RequestHeader(value = "Authorization", required = false) String token
    ) {
        try {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

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
                                    @RequestBody @Valid SpeakerCreateRequest speaker,
                                    @RequestHeader(value = "Authorization", required = false) String token
                                    ) throws NotFoundException {
        try {
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

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