package event.sync.dto.speaker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SpeakerCreateRequest {
    private String fullName;
    private String profilePicture;
    private String biography;
    private List<SpeakerLinkRequest> links;
}
