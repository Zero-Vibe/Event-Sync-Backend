package event.sync.service;

import event.sync.dto.room.RoomRequest;
import event.sync.dto.room.RoomResponse;
import event.sync.repository.OrganizerRepository;
import event.sync.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final OrganizerRepository organizerRepository;

    public RoomService(RoomRepository roomRepository, OrganizerRepository organizerRepository) {
        this.roomRepository = roomRepository;
        this.organizerRepository = organizerRepository;
    }

    public Optional<RoomResponse> findAll() {
        return Optional.ofNullable(roomRepository.getAllRooms()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Rooms found")));
    }

    public Optional<RoomResponse> createRoom(RoomRequest roomRequest, UUID organizerId) {
        organizerRepository.findById(organizerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Organizer not found"));

        return Optional.ofNullable(roomRepository.createRoom(roomRequest))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room Not Created"));
    }
}