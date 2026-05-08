package event.sync.service;

import event.sync.model.Session;
import event.sync.repository.SessionRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class SessionService {
    private SessionRepository sessionRepository;

    public Session getSession(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found: " + sessionId));
    }

    public List<Session> getAllSession(UUID eventId) {
        return sessionRepository.getAll(eventId);
    }
}
