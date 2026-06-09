package event.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import event.sync.model.enums.LinkPlatform;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "speaker_links")
public class SpeakerLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "speaker_id", referencedColumnName = "id")
    private Speaker speaker;
    public UUID getSpeakerId() { return (speaker != null) ? speaker.getId() : null; }

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LinkPlatform platform = LinkPlatform.OTHER;

    @Column(nullable = false)
    private String url;

    @Column
    private String label;
}