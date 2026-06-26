package event.sync.dto.speaker;

import event.sync.model.Speaker;
import event.sync.model.SpeakerLink;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SpeakerCreateRequest {
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Picture is needed")
    private String base64Picture;

    private String biography;

    private List<@Valid SpeakerLinkRequest> links;

    public static Speaker toSpeaker(SpeakerCreateRequest speakerCreateRequest) {
        Speaker speaker = Speaker.builder()
                .firstName(speakerCreateRequest.getFirstName())
                .lastName(speakerCreateRequest.getLastName())
                .pictureFileName((speakerCreateRequest.getLastName() != null) ?
                        (speakerCreateRequest.getBase64Picture()).contains("base64") ?
                        "" : speakerCreateRequest.getBase64Picture()
                        : "")
                .biography(speakerCreateRequest.getBiography())
                .build();
        List<SpeakerLink> speakerLinks = new ArrayList<>();
        for (SpeakerLinkRequest speakerLinkRequest : speakerCreateRequest.getLinks()) {
            speakerLinks.add(SpeakerLinkRequest.toSpeakerLink(speaker,  speakerLinkRequest));
        }
        speaker.setLinks(speakerLinks);
        return speaker;
    };
}
