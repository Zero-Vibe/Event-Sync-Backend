package event.sync.dto.session;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class SessionRegisterRequest {
    @NotNull(message = "sessionId must be provided") private UUID sessionId;
    @NotNull(message = "userId must bes provided") private UUID userId;
}
