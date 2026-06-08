package event.sync.service;

import event.sync.dto.room.RoomRequest;
import event.sync.dto.room.RoomResponse;
import event.sync.exception.NotFoundException;
import event.sync.model.Room;
import event.sync.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;

    public List<RoomResponse> findAll() {
        return roomRepository.findAll()
                .stream()
                .map(RoomResponse::fromRoom)
                .toList();
    }

    @Transactional
    public RoomResponse save(RoomRequest roomRequest) {
        if (roomRepository.findByName(roomRequest.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room already exists");
        }
        Room room = Room.builder()
                .name(roomRequest.getName())
                .sessions(new ArrayList<>())
                .build();

        return RoomResponse.fromRoom(roomRepository.save(room));
    }

    public Room findById(UUID id) throws NotFoundException {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Room not found"));
    }

    @Transactional
    public Room update(UUID id, RoomRequest roomRequest) throws NotFoundException {
        Room room = findById(id);
        room.setName(roomRequest.getName() == null ||  roomRequest.getName().isBlank() ? room.getName() : roomRequest.getName());
        return roomRepository.save(room);
    }

    @Transactional
    public void delete(UUID id) throws NotFoundException {
        findById(id);
        roomRepository.deleteById(id);
    }
}