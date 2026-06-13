package event.sync.dto.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateRequest {
    private UUID eventId;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private UUID roomId;
    private Integer capacity;
    private List<UUID> speakersId;
}