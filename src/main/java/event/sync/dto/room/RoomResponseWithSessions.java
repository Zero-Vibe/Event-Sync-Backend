package event.sync.dto.room;

import event.sync.model.Room;
import event.sync.model.Session;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomResponseWithSessions {
    private UUID id;
    private String name;
    private List<Session> sessions;

    public static RoomResponseWithSessions fromRoom(Room room) {
        return new RoomResponseWithSessions(room.getId(), room.getName(), room.getSessions());
    }
}
