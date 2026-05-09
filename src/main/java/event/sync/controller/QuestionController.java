package event.sync.controller;

import event.sync.dto.question.QuestionCreateRequest;
import event.sync.service.EventService;
import event.sync.service.QuestionService;
import event.sync.service.SessionService;
import event.sync.validator.QuestionCreateValidator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Controller
@RequestMapping("/events")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class QuestionController {

    private final QuestionService questionService;
    private final EventService eventService;
    private final QuestionCreateValidator questionCreateValidator;
    private final SessionService sessionService;

    @GetMapping("/{eventId}/sessions/{sessionId}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable UUID eventId,
                                       @PathVariable UUID sessionId) {
        try {
            eventService.findById(eventId);
            sessionService.findById(sessionId);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(questionService.getSessionQuestions(sessionId));
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

    @PostMapping("/{eventId}/sessions/{sessionId}/questions")
    public ResponseEntity<?> postQuestion(@PathVariable UUID eventId,
                                          @PathVariable UUID sessionId,
                                          @RequestBody QuestionCreateRequest question) {
        try {
            questionCreateValidator.validate(question);
            eventService.findById(eventId);
            sessionService.findById(sessionId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(questionService.create(sessionId, question));
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

    @PostMapping("/{eventId}/sessions/{sessionId}/questions/{questionId}/vote")
    public ResponseEntity<?> updateVote(@PathVariable UUID eventId,
                                        @PathVariable UUID sessionId,
                                        @PathVariable UUID questionId,
                                        @RequestParam boolean upvote) {
        try {
            eventService.findById(eventId);
            sessionService.findById(sessionId);
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(questionService.updateVote(questionId, upvote));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(e.getReason());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }}
