package event.sync.service;

import event.sync.dto.auth.LoginRequest;
import event.sync.dto.auth.LoginResponse;
import event.sync.model.Organizer;
import event.sync.repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OrganizerRepository organizerRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        Organizer organizer = organizerRepository
                .findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!BCrypt.checkpw(request.getPassword(), organizer.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(organizer.getId(), organizer.getEmail());

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("bearer")
                .expiresIn(jwtService.getExpirationSeconds())
                .build();
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Invalid email or password");
        }
    }
}