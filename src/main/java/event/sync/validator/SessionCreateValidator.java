package event.sync.validator;

import event.sync.dto.session.SessionCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class SessionCreateValidator {
    public void validate(SessionCreateRequest session) {
        List<String> errors = new ArrayList<>();
        if (session.getTitle() == null || session.getTitle().isBlank()) {
            errors.add("Title is required");
        }
        if (session.getDescription() == null || session.getDescription().isBlank()) {
            errors.add("Description is required");
        }
        if (session.getStartTime() == null || session.getEndTime() == null) {
            errors.add("Start and end time is required");
        } else if (session.getStartTime().isAfter(session.getEndTime())) {
            errors.add("Start time is after end time");
        }
        if (session.getCapacity() == null || session.getCapacity() <= 0) {
            errors.add("Capacity is required and cannot be less than 1");
        }
        if (session.getRoomId() == null) {
            errors.add("Room ID is required");
        }
        if (session.getSpeakersId() == null || session.getSpeakersId().isEmpty()) {
            errors.add("No speakers are provided");
        }
        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join(", ", errors));
        }
    }
}
