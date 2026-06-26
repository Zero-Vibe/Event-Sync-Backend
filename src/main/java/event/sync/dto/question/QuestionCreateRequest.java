package event.sync.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCreateRequest {
    @NotBlank(message = "Question must not be empty") private String content;
    @NotNull(message = "Should explicitly indicate anonymity") private Boolean isAnonymous;
}
