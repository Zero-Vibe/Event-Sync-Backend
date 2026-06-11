package event.sync.service;

import event.sync.dto.question.QuestionCreateRequest;
import event.sync.exception.NotFoundException;
import event.sync.model.Event;
import event.sync.model.Question;
import event.sync.model.Session;
import event.sync.repository.QuestionRepository;
import event.sync.repository.UserRepository;
import event.sync.specification.FilterSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public Page<Question> getAllQuestions(UUID sessionId, int page, int size, String sortField, String sortOrder, String filterJson) {
        Sort sort = sortOrder.equalsIgnoreCase("DESC") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Question> specification = FilterSpecification.parseSpecificationJson(filterJson);
        return questionRepository.findAll(specification, pageable);
    }


    public List<Question> getSessionQuestions(UUID sessionId) {
        return questionRepository.getQuestionsBySession_Id(sessionId);
    }

    public List<Question> getMany(List<UUID> ids) {
        return questionRepository.findAllById(ids);
    }

    @Transactional
    public Question save(Session session, QuestionCreateRequest question) throws NotFoundException {
        return questionRepository.save(Question.builder()
                        .session(session)
                        .content(question.getContent())
                        .user((question.getAuthorName() == null || question.getAuthorName().isBlank())
                                ? userRepository.findByName(question.getAuthorName())
                                    .orElseThrow(() -> new NotFoundException("Specified user not found"))
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
        return questionRepository.updateVote(questionId, upvote).getUpvotes();
    }

    public void deleteById(UUID questionId) {
        questionRepository.deleteById(questionId);
    }

    public void deleteMany(List<UUID> questionIds) {
        questionRepository.deleteAllById(questionIds);
    }
}