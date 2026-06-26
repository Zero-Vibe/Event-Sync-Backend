package event.sync.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email cannot be empty or blank")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Password cannot be empty") private String password;
}