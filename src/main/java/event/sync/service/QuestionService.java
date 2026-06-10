package event.sync.service;

import event.sync.dto.question.QuestionCreateRequest;
import event.sync.exception.NotFoundException;
import event.sync.model.Question;
import event.sync.model.Session;
import event.sync.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getSessionQuestions(UUID sessionId) {
        return questionRepository.getQuestionsBySession_Id(sessionId);
    }

    @Transactional
    public Question save(Session session, QuestionCreateRequest request) {
        return questionRepository.save(Question.builder()
                .session(session)
                .content(request.getContent())
                .authorName((request.getAuthorName() != null && !request.getAuthorName().isBlank())
                        ? request.getAuthorName()
                        : null)
                .upvotes(0)
                .createdAt(LocalDateTime.now())
                .build());
    }

    public Question findById(UUID id) throws NotFoundException {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question not found: " + id));
    }

    @Transactional
    public int updateVote(UUID questionId, boolean upvote) throws NotFoundException {
        Question initialQuestion = findById(questionId);
        if (initialQuestion.getUpvotes() <= 0 && !upvote) {
            return 0;
        }
        return questionRepository.updateVote(questionId, upvote);
    }
}
