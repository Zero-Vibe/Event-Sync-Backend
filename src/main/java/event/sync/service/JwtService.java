package event.sync.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    @Getter
    private final int expirationSeconds;

    public JwtService(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.expiration:3600}") int expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(UUID organisateurId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + (long) expirationSeconds * 1000);

        return Jwts.builder()
                .subject(organisateurId.toString())
                .claim("email", email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

}