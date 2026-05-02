package event.sync.model;

import event.sync.model.enums.LinkType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakerLink {

    private UUID id;
    private UUID speakerId;

    @Builder.Default
    private LinkType type = LinkType.OTHER;

    private String url;
    private String label;

    @Builder.Default
    private short order = 0;
}