package event.sync.controller;

import event.sync.dto.auth.*;
import event.sync.service.AuthService;
import event.sync.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody @Valid RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/authStatus")
    public ResponseEntity<?> checkAuth(@RequestHeader(name = "Authorization") String token) {
        if  (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
        jwtService.decodeToken(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}