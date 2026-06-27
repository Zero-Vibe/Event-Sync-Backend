package event.sync.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@ToString
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false, name = "picture_url")
    @JsonIgnore
    private String pictureFileName;

    @Transient
    private String base64Picture;

    @Column(length = 500)
    private String biography;

    @Builder.Default
    @OneToMany(mappedBy = "speaker", fetch = FetchType.LAZY)
    private List<SpeakerLink> links = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "speakers", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Builder.Default
    private List<Session> sessions = new ArrayList<>();
}