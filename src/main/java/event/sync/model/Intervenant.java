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
public class Intervenant {

    private UUID id;
    private String nomComplet;
    private String photoProfil;
    private String biographie;

    @Builder.Default
    private List<IntervenantLien> liens = new ArrayList<>();

    @Builder.Default
    private List<UUID> sessionIds = new ArrayList<>(); // références aux sessions

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}