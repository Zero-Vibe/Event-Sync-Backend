package event.sync.dto.speaker;

import event.sync.model.Speaker;
import event.sync.model.SpeakerLink;
import event.sync.model.enums.LinkPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@AllArgsConstructor
public class SpeakerLinkRequest {
    @NotNull(message = "Platform must be set")
    private LinkPlatform platform;

    @NotBlank(message = "URL must be set")
    @Pattern(
            regexp = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$",
            message = "Platform URL format is invalid"
    )
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
