package event.sync.dto.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateRequest {
    private String title;
    private String description;
    private UUID roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private List<UUID> speakersId;
}