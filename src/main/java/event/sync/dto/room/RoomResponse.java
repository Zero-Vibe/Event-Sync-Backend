package event.sync.dto.room;

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
}
