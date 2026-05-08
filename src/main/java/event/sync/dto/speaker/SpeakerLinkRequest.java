package event.sync.dto.speaker;

import event.sync.model.enums.LinkPlatform;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SpeakerLinkRequest {
    private LinkPlatform platform;
    private String url;
    private String label;
}
