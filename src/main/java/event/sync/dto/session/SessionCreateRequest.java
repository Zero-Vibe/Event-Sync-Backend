package event.sync.dto.session;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionCreateRequest {
    @NotNull(message = "Property \"eventId\" not set")
    private UUID eventId;

    @NotBlank(message = "Property \"title\" cannot be empty")
    private String title;

    @NotNull(message = "Property \"description\" cannot be empty")
    private String description;

    @NotNull(message = "Property \"startTime\" must be provided")
    private Instant startTime;

    @NotNull(message = "Property \"endTime\" must be provided")
    private Instant endTime;

    @AssertTrue(message = "startTime must be before endTime")
    private boolean isStartTimeValid() {
        return startTime.isBefore(endTime);
    };

    @NotNull(message = "Property \"roomId\" not set")
    private UUID roomId;

    @NotNull(message = "Property \"capacity\" must be provided")
    @Positive(message = "Property \"capacity\" cannot be 0 or lower")
    private Integer capacity;

    @NotEmpty(message = "Property \"speakersId\" not set")
    private List<UUID> speakersId;
}