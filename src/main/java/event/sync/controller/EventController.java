package event.sync.controller;

import event.sync.dto.event.EventCreateRequest;
import event.sync.exception.BadRequestException;
import event.sync.exception.NotFoundException;
import event.sync.model.Event;
import event.sync.service.AuthService;
import event.sync.service.EventService;
import event.sync.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final JwtService jwtService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false, value = "_start", defaultValue = "0") Integer start,
                                              @RequestParam(required = false, value = "_end", defaultValue = "10") Integer end,
                                              @RequestParam(required = false, value = "_sort", defaultValue = "start_date_time") String sort,
                                              @RequestParam(required = false, value = "_order", defaultValue = "ASC") String order,
                                              @RequestParam(required = false, value = "filter", defaultValue = "{}") String filterJson,
                                              @RequestParam(required = false, value = "id") List<UUID> ids
                                              ) {
        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(eventService.getMany(ids));
        }

        int pageSize = end - start;
        int pageNumber = start / pageSize;

        Page<Event> pagedResult = eventService.getAll(pageNumber, pageSize, sort, order, filterJson);

        return ResponseEntity.status(HttpStatus.OK)
                .header("X-Total-Count", String.valueOf(pagedResult.getTotalElements()))
                .header("Content-Type", "application/json")
                .body(pagedResult.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable UUID id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(eventService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Event> create(
            @RequestBody EventCreateRequest request,
            @RequestHeader("Authorization") String token
    ) throws NotFoundException, BadRequestException {
        Claims claims = jwtService.decodeToken(token);
        authService.checkIfAdmin(claims);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(eventService.create(request,
                        UUID.fromString(claims.getSubject())
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> update(
            @PathVariable UUID id,
            @RequestBody EventCreateRequest request,
            @RequestHeader("Authorization") String token
    )  throws NotFoundException, BadRequestException {
        Claims claims = jwtService.decodeToken(token);
        authService.checkIfAdmin(claims);
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(eventService.update(request,
                        id
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token
    ) throws NotFoundException {
        Claims claims = jwtService.decodeToken(token);
        authService.checkIfAdmin(claims);
        eventService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}