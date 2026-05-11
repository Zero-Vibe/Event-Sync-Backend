package event.sync.model;

import event.sync.model.enums.LinkPlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SpeakerLink {

    private UUID id;
    private UUID speakerId;

    @Builder.Default
    private LinkPlatform platform = LinkPlatform.OTHER;

    private String url;
    private String label;

    @Builder.Default
    private short order = 0;
}