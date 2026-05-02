package event.sync.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    private UUID id;
    private UUID sessionId;     // référence à Session.id
    private String contenu;
    private String nomAuteur;   // null = anonyme

    @Builder.Default
    private int upvotes = 0;

    private LocalDateTime createdAt;
}