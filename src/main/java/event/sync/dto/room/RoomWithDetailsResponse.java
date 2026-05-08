package event.sync.dto.room;

import event.sync.model.Session;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomWithDetailsResponse {
    private String id;
    private String name;
    private List<Session> session;

}
