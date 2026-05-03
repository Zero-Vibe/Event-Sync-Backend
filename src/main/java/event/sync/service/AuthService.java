package event.sync.service;

import event.sync.dto.auth.*;
import event.sync.model.Organizer;
import event.sync.repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public LoginResponse register(RegisterRequest request) {
        if (organizerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        LocalDateTime now = LocalDateTime.now();
        Organizer organizer = Organizer.builder()
                .id(UUID.randomUUID())
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .name(request.getName())
                .createdAt(now)
                .updatedAt(now)
                .build();

        organizerRepository.addOrganizer(organizer);

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

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }
}