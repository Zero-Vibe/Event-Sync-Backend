package event.sync.dto.speaker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SpeakerCreateRequest {
    private String fullName;
    private String profilePicture;
    private String biography;
    private List<SpeakerLinkRequest> speakerLinks;
}
