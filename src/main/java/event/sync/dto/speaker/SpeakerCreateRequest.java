package event.sync.dto.speaker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SpeakerCreateRequest {
    private String firstName;
    private String lastName;
    private String pictureUrl;
    private String biography;
    private List<SpeakerLinkRequest> links;
}
