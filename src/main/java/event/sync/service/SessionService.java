package event.sync.service;

import event.sync.dto.session.SessionCreateRequest;
import event.sync.exception.NotFoundException;
import event.sync.model.Event;
import event.sync.model.Room;
import event.sync.model.Session;
import event.sync.repository.EventRepository;
import event.sync.repository.RoomRepository;
import event.sync.repository.SessionRepository;
import event.sync.repository.SpeakerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    private final SpeakerRepository speakerRepository;

    public Session findById(UUID sessionId) throws NotFoundException {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionId));
    }

    public List<Session> getAll(UUID eventId) {
        return sessionRepository.findAllByEvent_Id(eventId);
    }

    @Transactional
    public Session create(SessionCreateRequest sessionDto) throws NotFoundException {

        Session session = Session.builder()
                .event(eventRepository.findById(sessionDto.getEventId())
                        .orElseThrow(() -> new NotFoundException("Event not found")))
                .room(roomRepository.findById(sessionDto.getRoomId())
                        .orElseThrow(() -> new NotFoundException("Room not found")))
                .title(sessionDto.getTitle())
                .description(sessionDto.getDescription())
                .startTime(sessionDto.getStartTime())
                .endTime(sessionDto.getEndTime())
                .capacity(sessionDto.getCapacity())
                .speakers(speakerRepository.findAllById(sessionDto.getSpeakersId()))
                .build();

        return sessionRepository.save(session);
    }

    @Transactional
    public Session update(UUID sessionId, SessionCreateRequest newSession) throws NotFoundException {
        Session session = findById(sessionId);
        roomRepository.findById(session.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specified room does not exist: " + session.getRoomId()));
        if (newSession.getEventId() != null) {
            session.setEvent(eventRepository.findById(newSession.getEventId()).orElseThrow(() -> new NotFoundException("Event not found: " + session.getEventId())));
        }
        if (newSession.getRoomId() != null) {
            session.setRoom(roomRepository.findById(newSession.getRoomId()).orElseThrow(() -> new NotFoundException("Specified room does not exist: " + newSession.getRoomId())));
        }
        if (newSession.getSpeakersId() != null && !newSession.getSpeakersId().isEmpty()) {
            session.setSpeakers(speakerRepository.findAllById(newSession.getSpeakersId()));
        }
        return sessionRepository.save(Session.builder()
                .id(session.getId())
                .event(session.getEvent())
                .room(session.getRoom())
                .title((newSession.getTitle() != null && !newSession.getTitle().isBlank()) ? newSession.getTitle() : session.getTitle())
                .description((newSession.getDescription() != null && !newSession.getTitle().isBlank()) ? newSession.getDescription() : session.getDescription())
                .startTime((newSession.getStartTime() != null) ? newSession.getStartTime() : session.getStartTime())
                .endTime((newSession.getEndTime() != null) ? newSession.getEndTime() : session.getEndTime())
                .capacity((newSession.getCapacity() != null) ? newSession.getCapacity() : session.getCapacity())
                .status(session.getStatus())
                .speakers(session.getSpeakers())
                .build());
    }

    @Transactional
    public void delete(UUID sessionId) throws NotFoundException {
        findById(sessionId);
        sessionRepository.deleteById(sessionId);
    }
}
