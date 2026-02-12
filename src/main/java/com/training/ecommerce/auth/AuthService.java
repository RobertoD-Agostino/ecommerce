package com.training.ecommerce.auth;


import com.training.ecommerce.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public String authenticate(LoginRequest request) {
        // Questa riga controlla automaticamente email e password nel DB
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // Se arriviamo qui, l'utente è valido. Generiamo il token.
        return tokenService.generateToken(auth);
    }
}
