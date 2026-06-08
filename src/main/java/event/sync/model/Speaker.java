package event.sync.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "speakers")
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false)
    private String pictureUrl;

    @Column(length = 500)
    private String biography;

    @Builder.Default
    @OneToMany(mappedBy = "speaker", fetch = FetchType.LAZY)
    private List<SpeakerLink> links = new ArrayList<>();
}