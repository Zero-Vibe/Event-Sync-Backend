package event.sync.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {

    private String title;
    private String description;
    private Instant startDateTime;
    private Instant endDateTime;
    private String location;
}