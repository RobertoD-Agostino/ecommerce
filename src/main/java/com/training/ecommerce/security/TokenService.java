package com.training.ecommerce.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder encoder;

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        // Estraiamo i ruoli dell'utente (es: ROLE_USER, ROLE_ADMIN)
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        // Creiamo il contenuto del token (Payload)
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self") // Chi ha emesso il token
                .issuedAt(now)  // Quando è stato creato
                .expiresAt(now.plus(1, ChronoUnit.HOURS)) // Scade tra un'ora
                .subject(authentication.getName()) // Il "proprietario" (email)
                .claim("scope", scope) // I suoi permessi
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
