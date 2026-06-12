package event.sync.dto.speaker;

import event.sync.model.Speaker;
import event.sync.model.SpeakerLink;
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

    public static SpeakerLink toSpeakerLink(Speaker speaker, SpeakerLinkRequest speakerLinkRequest) {
        return SpeakerLink.builder()
                .speaker(speaker)
                .platform(speakerLinkRequest.getPlatform())
                .url(speakerLinkRequest.getUrl())
                .label(speakerLinkRequest.getLabel())
                .build();
    }
}
