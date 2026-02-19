package com.training.ecommerce.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.training.ecommerce.config.RsaKeyProperties;
import com.training.ecommerce.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RsaKeyProperties rsaKeys; // Iniettiamo il record con le chiavi

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // 1. PUBBLICI: Chiunque può accedere
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()

                        // 2. SOLO ADMIN: Solo chi ha il ruolo ADMIN nel token
                        // Esempio: Creare, modificare o eliminare prodotti
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAuthority("SCOPE_ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/products/**").hasAuthority("SCOPE_ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/products/**").hasAuthority("SCOPE_ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/products/**").hasAuthority("SCOPE_ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("SCOPE_ROLE_ADMIN")

                        // Esempio: Gestione ordini globale
//                        .requestMatchers("/api/v1/admin/**").hasAuthority("SCOPE_ROLE_ADMIN")

                        // 3. AUTENTICATI: Qualsiasi utente loggato (User o Admin)
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exceptions -> exceptions
                        // Caso 1: Token mancante o non valido (Il tuo caso attuale)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{ \"errore\": \"NON_AUTENTICATO\", \"messaggio\": \"Token mancante o scaduto. Effettua il login per continuare.\" }");
                        })
                        // Caso 2: Token presente ma sei un User semplice (Il caso di prima)
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{ \"errore\": \"ACCESSO_NEGATO\", \"messaggio\": \"Permessi insufficienti. Solo l'admin può eliminare prodotti.\" }");
                        })
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

//    Stateless: Abbiamo impostato SessionCreationPolicy.STATELESS. Significa che il server non "ricorda" chi sei tra una richiesta e l'altra. L'unico modo per essere riconosciuti è inviare il JWT nell'header di ogni richiesta.
//    Resource Server: Con oauth2ResourceServer, Spring intercetta automaticamente ogni chiamata, estrae il token, lo decodifica con la publicKey e verifica se è valido e non scaduto.
//    Encoder/Decoder: Sono i "traduttori". L'encoder serve per generare il token quando l'utente fa il login, il decoder serve per leggerlo quando l'utente fa un acquisto.
}
