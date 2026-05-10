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
        if (speaker.getFullName() == null || speaker.getFullName().isEmpty()) {
            errors.add("Full name is required");
        }
        if (speaker.getProfilePicture() == null || speaker.getProfilePicture().isEmpty()) {
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
