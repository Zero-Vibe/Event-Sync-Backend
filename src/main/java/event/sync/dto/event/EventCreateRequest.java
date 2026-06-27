package event.sync.dto.event;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {
    @NotBlank(message = "title must be set and unique") private String title;
    private String description;
    @NotNull(message = "startDateTime must be set") private Instant startDateTime;
    @NotNull(message = "endDateTime must be set") private Instant endDateTime;
    @AssertTrue(message = "startDateTime must be before endDateTime")
    private boolean isStartTimeValid() {
        return startDateTime.isBefore(endDateTime);
    };
    @NotBlank(message = "Location must be set") private String location;
}