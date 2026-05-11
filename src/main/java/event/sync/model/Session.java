package event.sync.model;

import event.sync.model.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Session {

    private UUID id;
    private UUID eventId;
    private UUID roomId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer capacity;

    @Builder.Default
    private SessionStatus status = SessionStatus.DRAFT;

    @Builder.Default
    private List<Speaker> speakers = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isLive() {
        return SessionStatus.LIVE.equals(this.status);
    }
}