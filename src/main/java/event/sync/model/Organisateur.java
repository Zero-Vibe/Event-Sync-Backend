package event.sync.model;

import event.sync.model.enums.RoleUtilisateur;
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
public class Organisateur {

    private UUID id;
    private String email;
    private String passwordHash;
    private String nom;

    @Builder.Default
    private RoleUtilisateur role = RoleUtilisateur.ORGANISATEUR;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}