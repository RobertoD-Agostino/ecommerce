package com.training.ecommerce.utils;

import com.training.ecommerce.entities.User;
import com.training.ecommerce.exceptions.UserException;
import com.training.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtils {

    private final UserRepository userRepo;

    public User findUserByEmail(String email)throws RuntimeException{
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UserException("Utente con email " + email + " non trovato", HttpStatus.NOT_FOUND));
    }
}
