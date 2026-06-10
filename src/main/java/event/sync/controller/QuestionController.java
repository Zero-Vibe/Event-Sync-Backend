package event.sync.controller;

import event.sync.dto.question.QuestionCreateRequest;
import event.sync.exception.BadRequestException;
import event.sync.exception.NotFoundException;
import event.sync.model.Session;
import event.sync.service.*;
import event.sync.validator.QuestionCreateValidator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Controller
@RequestMapping("/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuestionController {

    private final QuestionService questionService;
    private final EventService eventService;
    private final QuestionCreateValidator questionCreateValidator;
    private final SessionService sessionService;
    private final AuthService authService;
    private final JwtService jwtService;

    @GetMapping("/{eventId}/sessions/{sessionId}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable UUID eventId,
                                          @PathVariable UUID sessionId) throws NotFoundException {
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
                                          @RequestBody QuestionCreateRequest question
    ) throws NotFoundException, BadRequestException {
        try {
            questionCreateValidator.validate(question);
            eventService.findById(eventId);
            Session session = sessionService.findById(sessionId);

            if (!session.isLive()) {
                throw new BadRequestException("Session is not live");
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(questionService.save(session, question));
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
                                        @RequestParam boolean upvote
    ) throws NotFoundException, BadRequestException {
        try {

            eventService.findById(eventId);
            Session session = sessionService.findById(sessionId);

            if (!session.isLive()) {
                throw new BadRequestException("Session is not live");
            }
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
    }
}