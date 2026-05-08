package event.sync.service;

import event.sync.dto.session.SessionCreateRequest;
import event.sync.model.Session;
import event.sync.repository.RoomRepository;
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
    private RoomRepository roomRepository;

    public Session findById(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found: " + sessionId));
    }

    public List<Session> getAllSessions(UUID eventId) {
        return sessionRepository.getAll(eventId);
    }

    public Session createSession(UUID eventId, SessionCreateRequest session) {
        roomRepository.findById(session.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specified room does not exist: " + session.getRoomId()));
        return sessionRepository.create(eventId, session);
    }

    public Session updateSession(UUID eventId, SessionCreateRequest session) {
        roomRepository.findById(session.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specified room does not exist: " + session.getRoomId()));
        return sessionRepository.update(eventId, session);
    }
}
