package event.sync.dto.speaker;

import event.sync.model.Speaker;
import event.sync.model.SpeakerLink;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SpeakerCreateRequest {
    private String firstName;
    private String lastName;
    private String base64Picture;
    private String biography;
    private List<SpeakerLinkRequest> links;

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
