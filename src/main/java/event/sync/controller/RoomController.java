package event.sync.controller;

import event.sync.dto.room.RoomRequest;
import event.sync.dto.room.RoomResponse;
import event.sync.exception.ConflictException;
import event.sync.exception.NotFoundException;
import event.sync.repository.UserRepository;
import event.sync.service.AuthService;
import event.sync.service.JwtService;
import event.sync.service.RoomService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(exposedHeaders = "X-Total-Count")
public class RoomController {
    private final RoomService roomService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/rooms")
    public ResponseEntity<?> getRooms(@RequestParam(required = false, value = "_start", defaultValue = "0") Integer start,
                                      @RequestParam(required = false, value = "_end", defaultValue = "10") Integer end,
                                      @RequestParam(required = false, value = "_sort", defaultValue = "name") String sort,
                                      @RequestParam(required = false, value = "_order", defaultValue = "ASC") String order,
                                      @RequestParam(required = false, value = "filter", defaultValue = "{}") String filterJson,
                                      @RequestParam(required = false, value = "id") List<UUID> ids
    ) {
        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(roomService.getMany(ids));
        }

        int pageSize = end - start;
        int pageNumber = start / pageSize;

        Page<RoomResponse> pagedResult = roomService.findAll(pageNumber, pageSize, sort, order, filterJson);

        return ResponseEntity.status(HttpStatus.OK)
                .header("X-Total-Count", String.valueOf(pagedResult.getTotalElements()))
                .header("Content-Type", "application/json")
                .body(pagedResult.getContent());

    }

    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(
            @RequestBody @Valid RoomRequest roomRequest,
            @RequestHeader("Authorization") String token
    )  throws NotFoundException, ConflictException {
        Claims claims = jwtService.decodeToken(token);
        authService.checkIfAdmin(claims);

        userRepository.findById(UUID.fromString(claims.getSubject())).orElseThrow(
                () -> new NotFoundException("User not found")
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(roomService.save(roomRequest));
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<?> getRoomWithDetails(@PathVariable UUID id) throws NotFoundException {
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(roomService.findById(id));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<?> updateRoom(
            @PathVariable UUID id,
            @RequestBody @Valid RoomRequest roomRequest,
            @RequestHeader("Authorization") String token
    ) throws NotFoundException {
        Claims claims = jwtService.decodeToken(token);
        authService.checkIfAdmin(claims);

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(roomService.update(id, roomRequest));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<?> deleteRoom(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token
    ) throws NotFoundException {
        Claims claims = jwtService.decodeToken(token);
        authService.checkIfAdmin(claims);

        roomService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}