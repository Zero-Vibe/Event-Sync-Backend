package event.sync.service;

import event.sync.dto.session.SessionCreateRequest;
import event.sync.exception.BadRequestException;
import event.sync.exception.NotFoundException;
import event.sync.model.*;
import event.sync.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    private final SpeakerRepository speakerRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public Session findById(UUID sessionId) throws NotFoundException {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionId));
    }

    public Page<Session> getAll(int pageNumber, int pageSize, String sort, String order, UUID eventId) {
        Sort sortCondition = order.equalsIgnoreCase("ASC") ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sortCondition);
        return sessionRepository.findAllByEvent_Id(eventId, pageable);
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
                .speakers(new ArrayList<>())
                .build();

        setSpeakersAndCheck(session, sessionDto.getSpeakersId());

        return sessionRepository.save(session);
    }

    @Transactional
    public Session update(UUID sessionId, SessionCreateRequest newSession) throws NotFoundException {
        Session session = findById(sessionId);

        roomRepository.findById(session.getRoomId())
                .orElseThrow(() -> new NotFoundException("Specified room does not exist: " + session.getRoomId()));

        session.setEvent(eventRepository.findById(newSession.getEventId()).orElseThrow(() -> new NotFoundException("Event not found: " + session.getEventId())));
        session.setRoom(roomRepository.findById(newSession.getRoomId()).orElseThrow(() -> new NotFoundException("Specified room does not exist: " + newSession.getRoomId())));

        session.setSpeakers(setSpeakersAndCheck(session, newSession.getSpeakersId()));

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
    public void delete(UUID eventId, UUID sessionId) throws NotFoundException {
        Session session = findById(sessionId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event not found: " + eventId));
        if (!event.getSessions().remove(session)) {
            throw new NotFoundException("Session not found in event: " + event.getTitle());
        }
        sessionRepository.deleteById(sessionId);
    }

    private List<Speaker> setSpeakersAndCheck(Session session, List<UUID> speakerIds) throws NotFoundException {
        List<Speaker> speakers = speakerRepository.findAllById(speakerIds);
        if (speakers.isEmpty()) {
            throw new NotFoundException("No speaker found");
        }
        if (speakers.size() != speakerIds.size()) {
            throw new NotFoundException("Speakers not found; IDs=["
                    + speakerIds.stream()
                    .filter(id -> !session.getSpeakers().stream()
                            .map(Speaker::getId).toList().contains(id))
                    .map(UUID::toString)
                    .toList()
                    + "]");
        }
        session.setSpeakers(speakers);
        return speakers;
    }

    public long getRegisterCount(UUID sessionId) {
        return registrationRepository.countSessionRegistrationBySessionId(sessionId);
    }

    @Transactional
    public void register(UUID sessionId, UUID userId) throws NotFoundException, BadRequestException {
        Optional<SessionRegistration> registration = registrationRepository.findBySession_idAndUser_id(sessionId, userId);
        if (registration.isPresent()) {
            throw new BadRequestException("Already registered to session");
        }
        registrationRepository.save(SessionRegistration.builder()
                .session(sessionRepository.findById(sessionId)
                        .orElseThrow(() -> new NotFoundException("Session not found: " + sessionId)))
                .user(userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException("User not found: " + userId)))
                .registrationTime(Instant.now())
                .build()
        );
    }

    @Transactional
    public void unregister(UUID sessionId, UUID userId) throws NotFoundException {
        registrationRepository.findBySession_idAndUser_id(sessionId, userId)
                .orElseThrow(() ->  new NotFoundException("No registration found for session"));
        registrationRepository.deleteSessionRegistrationsByIdAndUser_Id(sessionId, userId);
    }

}
