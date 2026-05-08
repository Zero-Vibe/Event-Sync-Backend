package event.sync.controller;

import event.sync.service.EventService;
import event.sync.service.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/events")
@AllArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final EventService eventService;

    @GetMapping("/{eventId}/sessions/{sessionId}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable UUID eventId,
                                       @PathVariable UUID sessionId) {
        try {
            eventService.findById(eventId);
            // TODO: sessionService.findById(sessionId)
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(questionService.getSessionQuestions(sessionId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }

}
