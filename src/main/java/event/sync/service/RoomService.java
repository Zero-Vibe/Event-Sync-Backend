package event.sync.service;

import event.sync.dto.room.RoomRequest;
import event.sync.dto.room.RoomResponse;
import event.sync.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Optional<RoomResponse> findAll() {
        return Optional.ofNullable(roomRepository.getAllRooms()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Rooms found")));
    }

    public Optional<RoomResponse> createRoom(RoomRequest roomRequest) {
        return Optional.ofNullable(roomRepository.createRoom(roomRequest))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room Not Created"));
    }
}
