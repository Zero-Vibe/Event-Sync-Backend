package event.sync.service;

import event.sync.dto.auth.*;
import event.sync.model.User;
import event.sync.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!BCrypt.checkpw(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getId(), user.isAdmin(), user.getEmail());

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("bearer")
                .expiresIn(jwtService.getExpirationSeconds())
                .build();
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        User user = userRepository.save(User.builder()
                .isAdmin(false)
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .name(request.getName())
                .joinDate(LocalDateTime.now())
                .build()
        );

        String token = jwtService.generateToken(user.getId(), user.isAdmin(), user.getEmail());

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("bearer")
                .expiresIn(jwtService.getExpirationSeconds())
                .build();
    }

    public void checkIfAdmin(Claims claims) {
        if (!claims.get("isAdmin").equals(Boolean.TRUE)) throw new PermissionDeniedException();
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Invalid email or password");
        }
    }

    public static class PermissionDeniedException extends RuntimeException {
        public PermissionDeniedException() {
            super("Admin access denied");
        }
    }

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }
}