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
        return questionRepository.create(sessionId, question)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch created question"));
    }
    
    public Question findById(UUID id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found: " + id));
    }

    public Question updateVote(UUID questionId, boolean upvote) {
        Question initialQuestion = findById(questionId);
        if (initialQuestion.getUpvotes() <= 0 && !upvote) {
            return initialQuestion;
        }
        return questionRepository.updateVote(questionId, upvote)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch updated question"));
    }
}