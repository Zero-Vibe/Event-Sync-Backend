package event.sync.validator;

import event.sync.dto.question.QuestionCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;


@Component
public class QuestionCreateValidator {
    public void validate(QuestionCreateRequest question) {
        if (question.getContent() == null || question.getContent().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question content is empty");
        }
    }
}
