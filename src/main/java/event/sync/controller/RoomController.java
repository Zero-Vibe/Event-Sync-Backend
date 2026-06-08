package event.sync.controller;

import event.sync.dto.room.RoomRequest;
import event.sync.exception.NotFoundException;
import event.sync.repository.UserRepository;
import event.sync.service.AuthService;
import event.sync.service.JwtService;
import event.sync.service.RoomService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/rooms")
    public ResponseEntity<?> getRooms() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(roomService.findAll());
        } catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(e.getReason());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }

    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(
            @RequestBody RoomRequest roomRequest,
            @RequestHeader("Authorization") String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            userRepository.findById(UUID.fromString(claims.getSubject())).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found")
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(roomService.save(roomRequest));
        } catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(e.getReason());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<?> getRoomWithDetails(@PathVariable UUID id) throws NotFoundException {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(roomService.findById(id));
        }  catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body((e.getReason()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<?> updateRoom(
            @PathVariable UUID id,
            @RequestBody RoomRequest roomRequest,
            @RequestHeader("Authorization") String token
    ) throws NotFoundException {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(roomService.update(id, roomRequest));
        } catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(e.getReason());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<?> deleteRoom(
            @PathVariable UUID id,
            @RequestHeader("Authorization") String token
    ) throws NotFoundException {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Claims claims = jwtService.decodeToken(token);
            authService.checkIfAdmin(claims);

            roomService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (ResponseStatusException e){
            return ResponseEntity.status(e.getStatusCode())
                    .header("Content-Type", "application/json")
                    .body(e.getReason());
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(e.getMessage());
        }
    }
}