package event.sync.controller;

import event.sync.dto.question.QuestionCreateRequest;
import event.sync.exception.BadRequestException;
import event.sync.exception.NotFoundException;
import event.sync.model.Event;
import event.sync.model.Question;
import event.sync.model.Session;
import event.sync.repository.QuestionRepository;
import event.sync.service.*;
import event.sync.validator.QuestionCreateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@CrossOrigin(exposedHeaders = "X-Total-Count")
public class QuestionController {

    private final QuestionService questionService;
    private final EventService eventService;
    private final QuestionCreateValidator questionCreateValidator;
    private final SessionService sessionService;
    private final JwtService jwtService;
    private final QuestionRepository questionRepository;
    private final AuthService authService;

    @GetMapping("/{eventId}/sessions/{sessionId}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable UUID eventId,
                                          @PathVariable UUID sessionId,
                                          @RequestParam(required = false, value = "_start", defaultValue = "0") Integer start,
                                          @RequestParam(required = false, value = "_end", defaultValue = "10") Integer end,
                                          @RequestParam(required = false, value = "_sort", defaultValue = "upvotes") String sort,
                                          @RequestParam(required = false, value = "_order", defaultValue = "ASC") String order,
                                          @RequestParam(required = false, value = "filter", defaultValue = "{}") String filterJson,
                                          @RequestParam(required = false, value = "id") List<UUID> ids
    ) throws NotFoundException {
        try {
            isEventRelated(eventId, sessionId);
            sessionService.findById(sessionId);

            if (ids != null && !ids.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(questionService.getMany(ids));
            }

            int pageSize = end - start;
            int pageNumber = start / pageSize;

            Page<Question> pagedResultPage = questionService.getAllQuestions(sessionId, pageNumber, pageSize, sort, order, filterJson);

            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .header("X-Total-Count", String.valueOf(pagedResultPage.getTotalElements()))
                    .body(pagedResultPage.getContent());
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
                                          @RequestBody QuestionCreateRequest question,
                                          @RequestHeader(name = "Authorization", required = true) String token
    ) throws NotFoundException, BadRequestException {
        try {
            if (token == null || token.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            UUID userId = UUID.fromString(jwtService.decodeToken(token).getSubject());

            questionCreateValidator.validate(question);
            isEventRelated(eventId, sessionId);
            Session session = sessionService.findById(sessionId);

            if (!session.isLive()) {
                throw new BadRequestException("Session is not live");
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(questionService.save(session, userId, question));
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
                                        @RequestParam boolean upvote,
                                        @RequestHeader(name = "Authorization", required = true) String token
    ) throws NotFoundException, BadRequestException {
        try {
            if (token == null || token.isBlank()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            jwtService.decodeToken(token);

            isEventRelated(eventId, sessionId);
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

    @DeleteMapping("/{eventId}/sessions/{sessionId}/questions/{questionId}")
    public ResponseEntity<?> delete(@PathVariable UUID eventId,
                                    @PathVariable UUID sessionId,
                                    @PathVariable UUID questionId,
                                    @RequestHeader("Authorization") String token
    ) throws NotFoundException, BadRequestException {
        authService.checkIfAdmin(jwtService.decodeToken(token));

        isEventRelated(eventId, sessionId);
        Session session = sessionService.findById(sessionId);
        Question question =  questionService.findById(questionId);
        if (!question.getSession().getId().equals(session.getId())) throw new BadRequestException("Question not found in session");
        questionService.deleteById(questionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{eventId}/sessions/{sessionId}/questions")
    public ResponseEntity<?> deleteMany(@PathVariable UUID eventId,
                                        @PathVariable UUID sessionId,
                                        @RequestBody List<UUID> questionIds,
                                        @RequestHeader("Authorization") String token
    ) throws NotFoundException, BadRequestException {
        authService.checkIfAdmin(jwtService.decodeToken(token));

        isEventRelated(eventId, sessionId);
        Session session = sessionService.findById(sessionId);
        List<Question> questions =  questionService.getMany(questionIds);
        for (Question question : questions) {
            if (!question.getSession().getId().equals(session.getId())) {
                throw new BadRequestException("Question not found in session");
            }
        }
        questionService.deleteMany(questionIds);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private void isEventRelated(UUID eventId, UUID sessionId) throws NotFoundException {
        Event event = eventService.findById(eventId);
        if (event.getSessions().stream()
                .noneMatch(s -> s.getId().equals(sessionId))) {
            throw new NotFoundException("Session not found in event: " + event.getTitle());
        }
    };
}