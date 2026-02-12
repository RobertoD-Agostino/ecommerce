package com.training.ecommerce.auth;


import com.training.ecommerce.entities.User;
import com.training.ecommerce.repositories.UserRepository;
import com.training.ecommerce.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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

    public String register(AuthRequest request) {
        // Creiamo l'utente usando i tuoi campi
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        // Inizializziamo le liste per evitare errori nel DB
        user.setRoles(new ArrayList<>());
        user.setOrderList(new ArrayList<>());
        user.setPurchasedItemList(new ArrayList<>());

        userRepository.save(user);
        return "Utente registrato con successo! Ora puoi fare il login.";
    }

    public String login(AuthRequest request) {
        // 1. Verifichiamo le credenziali
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // 2. Se OK, generiamo il token JWT
        return tokenService.generateToken(auth);
    }
}
