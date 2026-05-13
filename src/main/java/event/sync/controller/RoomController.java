package event.sync.controller;

import event.sync.dto.room.RoomRequest;
import event.sync.dto.room.RoomResponse;
import event.sync.dto.room.RoomWithDetailsResponse;
import event.sync.service.JwtService;
import event.sync.service.RoomService;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
public class RoomController {
    private final RoomService roomService;
    private final JwtService jwtService;

    public RoomController(RoomService roomService, JwtService jwtService) {
        this.roomService = roomService;
        this.jwtService = jwtService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getRooms() {
        try {
            return ResponseEntity.ok(roomService.findAll());
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
            UUID organizerId = UUID.fromString(claims.getSubject());

            return ResponseEntity.ok(roomService.createRoom(roomRequest, organizerId));
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
    public ResponseEntity<?> getRoomWithDetails(@PathVariable String id) {
        try {
            return ResponseEntity.ok(roomService.findRoomWithDetails(id));
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
            @PathVariable String id,
            @RequestBody RoomRequest roomRequest,
            @RequestHeader("Authorization") String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            jwtService.decodeToken(token);
            return ResponseEntity.ok(roomService.updateRoom(id, roomRequest));
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
            @PathVariable String id,
            @RequestHeader("Authorization") String token
    ) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            jwtService.decodeToken(token);
            roomService.deleteRoom(id);
            return ResponseEntity.status(204).build();
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