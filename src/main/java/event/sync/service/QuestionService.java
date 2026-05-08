package event.sync.service;

import event.sync.model.Question;
import event.sync.repository.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getSessionQuestions(UUID sessionId) {
        return questionRepository.getSessionQuestions(sessionId);
    }
}