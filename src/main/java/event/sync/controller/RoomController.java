package event.sync.controller;

import event.sync.dto.room.RoomRequest;
import event.sync.dto.room.RoomResponse;
import event.sync.service.JwtService;
import event.sync.service.RoomService;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class RoomController {
    private final RoomService roomService;
    private final JwtService jwtService;

    public RoomController(RoomService roomService, JwtService jwtService) {
        this.roomService = roomService;
        this.jwtService = jwtService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<Optional<RoomResponse>> getRooms() {
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
}