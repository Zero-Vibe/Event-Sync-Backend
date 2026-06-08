package event.sync.dto.room;

import event.sync.model.Room;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponse {
    private UUID id;
    private String name;

    public static RoomResponse fromRoom(Room room) {
        return new RoomResponse(room.getId(), room.getName());
    }
}
