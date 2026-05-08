package event.sync.controller;

import event.sync.dto.room.RoomResponse;
import event.sync.model.Event;
import event.sync.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<Optional<RoomResponse>> getRooms(){
        return ResponseEntity.ok(roomService.findAll());
    }


}
