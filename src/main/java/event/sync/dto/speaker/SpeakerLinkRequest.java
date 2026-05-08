package event.sync.dto.speaker;

import event.sync.model.enums.LinkType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SpeakerLinkRequest {
    private LinkType linkType;
    private String url;
    private String label;
}
