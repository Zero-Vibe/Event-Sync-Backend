package event.sync.dto.session;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class SessionRegisterRequest {
    @NotNull private UUID sessionId;
    @NotNull private UUID userId;
}
