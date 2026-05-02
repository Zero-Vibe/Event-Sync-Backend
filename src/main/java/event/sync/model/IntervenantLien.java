package event.sync.model;

import event.sync.model.enums.LienType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntervenantLien {

    private UUID id;
    private UUID intervenantId;

    @Builder.Default
    private LienType type = LienType.OTHER;

    private String url;
    private String label;

    @Builder.Default
    private short ordre = 0;
}