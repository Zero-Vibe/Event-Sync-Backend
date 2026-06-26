package event.sync.dto.room;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomRequest {
    @NotBlank(message = "Room name is mandatory") private String name;
}
