package event.sync.dto.auth;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String password;
    private String name;
}
