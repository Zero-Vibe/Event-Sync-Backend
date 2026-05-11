package event.sync.dto.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SessionCreateRequest {
    private String title;
    private String description;
    private UUID roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;
    private List<UUID> speakersId;
}