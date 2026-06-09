package event.sync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Speaker {

    private UUID id;
    private String fullName;
    private String profilePicture;
    private String biography;

    @Builder.Default
    private List<SpeakerLink> links = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}