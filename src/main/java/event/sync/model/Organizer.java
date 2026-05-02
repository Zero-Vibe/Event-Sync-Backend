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
public class Organizer {
    private UUID id;
    private String email;
    private String passwordHash;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}