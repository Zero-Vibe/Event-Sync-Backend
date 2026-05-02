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
public class Room {

    private UUID id;
    private String name;

    @Builder.Default
    private List<Session> sessions = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}