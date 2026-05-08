package event.sync.service;

import event.sync.dto.question.QuestionCreateRequest;
import event.sync.model.Question;
import event.sync.repository.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getSessionQuestions(UUID sessionId) {
        return questionRepository.getSessionQuestions(sessionId);
    }

    public Question create(UUID sessionId, QuestionCreateRequest question) {
        return questionRepository.findById(questionRepository.create(sessionId, question))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found: " + question));
    }
}