package event.sync.service;

import event.sync.dto.event.EventCreateRequest;
import event.sync.model.Event;
import event.sync.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    public Event findById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + id));
    }

    public Event create(EventCreateRequest request, UUID organizerId) {
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }

        return eventRepository.save(Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .createdBy(organizerId)
                .build());
    }

    public Event update(UUID id, EventCreateRequest request) {
        findById(id);

        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date must be after start date");
        }

        return eventRepository.update(id, Event.builder()
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .location(request.getLocation())
                        .build())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + id));
    }

    public void delete(UUID id) {
        findById(id);
        eventRepository.deleteById(id);
    }
}