package event.sync.service;

import event.sync.dto.event.EventCreateRequest;
import event.sync.exception.BadRequestException;
import event.sync.exception.NotFoundException;
import event.sync.model.Event;
import event.sync.model.User;
import event.sync.repository.EventRepository;
import event.sync.repository.UserRepository;
import event.sync.specification.FilterSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Page<Event> getAll(int page, int size, String sortField, String sortOrder, String filterJson) {
        Sort sort = sortOrder.equalsIgnoreCase("DESC") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Event> specification = FilterSpecification.parseSpecificationJson(filterJson);
        return eventRepository.findAll(specification, pageable);
    }

    public List<Event> getMany(List<UUID> ids) {
        return eventRepository.findAllById(ids);
    }

    public Event findById(UUID id) throws NotFoundException {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found: " + id));
    }

    @Transactional
    public Event create(EventCreateRequest request, UUID userId) throws NotFoundException, BadRequestException {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        return eventRepository.save(Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDateTime(request.getStartDate())
                .endDateTime(request.getEndDate())
                .location(request.getLocation())
                .createdBy(creator)
                .build());
    }

    @Transactional
    public Event update(EventCreateRequest request, UUID id) throws NotFoundException, BadRequestException {
        Event toUpdate = findById(id);

        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        return eventRepository.save(Event.builder()
                .id(id)
                .title((request.getTitle() != null && !request.getTitle().isBlank()) ? request.getTitle() : toUpdate.getTitle())
                .description((request.getDescription() != null && !request.getDescription().isBlank()) ? request.getDescription() : toUpdate.getDescription())
                .startDateTime((request.getStartDate() != null) ? request.getStartDate() : toUpdate.getStartDateTime())
                .endDateTime((request.getEndDate() != null) ? request.getEndDate() : toUpdate.getEndDateTime())
                .location((request.getLocation() != null && !request.getLocation().isBlank()) ? request.getLocation() : toUpdate.getLocation())
                .createdBy(toUpdate.getCreatedBy())
                .sessions(toUpdate.getSessions())
                .build());
    }

    @Transactional
    public void delete(UUID id) throws NotFoundException {
        findById(id);
        eventRepository.deleteById(id);
    }
}