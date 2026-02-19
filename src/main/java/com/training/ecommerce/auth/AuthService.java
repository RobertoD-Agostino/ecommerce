package com.training.ecommerce.auth;


import com.training.ecommerce.entities.Role;
import com.training.ecommerce.entities.User;
import com.training.ecommerce.repositories.RoleRepository;
import com.training.ecommerce.repositories.UserRepository;
import com.training.ecommerce.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
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
        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        List<Role> roles = new ArrayList<>();

        // LOGICA DI SCELTA:
        if (request.email().endsWith("@admin.com")) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("Ruolo non trovato"));
            roles.add(adminRole);
        } else {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Ruolo non trovato"));
            roles.add(userRole);
        }

        user.setRoles(roles);
        userRepository.save(user);
        return "Registrato!";
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
