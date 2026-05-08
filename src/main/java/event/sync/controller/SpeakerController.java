package event.sync.controller;

import event.sync.dto.speaker.SpeakerCreateRequest;
import event.sync.service.SpeakerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Controller
@RequestMapping("/speakers")
public class SpeakerController {

    private final SpeakerService speakerService;

    public SpeakerController(SpeakerService speakerService) {
        this.speakerService = speakerService;
    }

    @GetMapping("/{speakerId}")
    public ResponseEntity<?> findById(@PathVariable UUID speakerId) {
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
    public ResponseEntity<?> save(@RequestBody SpeakerCreateRequest speaker) {
        try {
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
}