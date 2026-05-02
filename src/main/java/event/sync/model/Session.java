package event.sync.model;

import event.sync.model.enums.SessionStatut;
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
public class Session {

    private UUID id;
    private UUID evenementId;   // référence à Evenement.id
    private UUID salleId;       // référence à Salle.id
    private String titre;
    private String description;
    private LocalDateTime heureDebut;
    private LocalDateTime heureFin;
    private Integer capacite;

    @Builder.Default
    private SessionStatut statut = SessionStatut.BROUILLON;

    @Builder.Default
    private List<Intervenant> intervenants = new ArrayList<>();

    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isLive() {
        return SessionStatut.EN_COURS.equals(this.statut);
    }
}