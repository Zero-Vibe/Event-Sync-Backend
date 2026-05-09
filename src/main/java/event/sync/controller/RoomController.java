package event.sync.controller;

import event.sync.dto.room.RoomRequest;
import event.sync.dto.room.RoomResponse;
import event.sync.dto.room.RoomWithDetailsResponse;
import event.sync.service.JwtService;
import event.sync.service.RoomService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
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
    public ResponseEntity<Optional<List<RoomResponse>>> getRooms() {
        return ResponseEntity.ok(roomService.findAll());
    }

    @PostMapping("/rooms")
    public ResponseEntity<Optional<RoomResponse>> createRoom(
            @RequestBody RoomRequest roomRequest,
            @RequestHeader("Authorization") String token
    ) {
        Claims claims = jwtService.decodeToken(token);
        UUID organizerId = UUID.fromString(claims.getSubject());

        return ResponseEntity.ok(roomService.createRoom(roomRequest, organizerId));
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<Optional<RoomWithDetailsResponse>> getRoomWithDetails(@PathVariable String id) {
        return ResponseEntity.ok(roomService.findRoomWithDetails(id));
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable String id,
            @RequestBody RoomRequest roomRequest,
            @RequestHeader("Authorization") String token
    ) {
        jwtService.decodeToken(token);
        return ResponseEntity.ok(roomService.updateRoom(id, roomRequest));
    }

    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable String id,
            @RequestHeader("Authorization") String token
    ) {
        jwtService.decodeToken(token);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}