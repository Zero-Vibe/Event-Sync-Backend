package event.sync.validator;

import event.sync.dto.speaker.SpeakerCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpeakerCreateValidator {
    public void validate(SpeakerCreateRequest speaker) {
        List<String> errors = new ArrayList<>();
        if (speaker.getFirstName() == null || speaker.getFirstName().isEmpty()) {
            errors.add("First name is required");
        }
        if (speaker.getBase64Picture() == null || speaker.getBase64Picture().isEmpty()) {
            errors.add("Profile Picture Link is required");
        }
        if (speaker.getBiography() == null || speaker.getBiography().isEmpty()) {
            errors.add("Biography is required");
        }
        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", errors));
        }
    }
}
