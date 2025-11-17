package com.training.ecommerce.services;

import com.training.ecommerce.dtos.UserDto;
import com.training.ecommerce.entities.User;
import com.training.ecommerce.exceptions.UserAlreadyExistsException;
import com.training.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;

    public UserDto createUser(User user) throws RuntimeException{
        if(userRepo.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException("L'utente " +user.getEmail() + " è già presente");
        }
        userRepo.save(user);
        return new UserDto(user.getId(),user.getFirstName(),user.getEmail());
    }

    public UserDto findUser(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente con email " + email + " non trovato"));
        return new UserDto(user.getId(), user.getFirstName(), user.getEmail());
    }



}

