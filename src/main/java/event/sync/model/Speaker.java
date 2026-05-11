package event.sync.model;

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
public class Speaker {

    private UUID id;
    private String fullName;
    private String profilePicture;
    private String biography;

    @Builder.Default
    private List<SpeakerLink> links = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}