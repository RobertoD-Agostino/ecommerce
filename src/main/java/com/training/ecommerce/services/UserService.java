package com.training.ecommerce.services;

import com.training.ecommerce.dtos.UserDto;
import com.training.ecommerce.entities.Cart;
import com.training.ecommerce.entities.User;
import com.training.ecommerce.exceptions.UserException;
import com.training.ecommerce.repositories.CartRepository;
import com.training.ecommerce.repositories.UserRepository;
import com.training.ecommerce.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepo;
    private final UserUtils userUtils;
    private final CartRepository cartRepo;

    public UserDto createUser(User user) throws RuntimeException{
        Cart userCart = new Cart();
        if(userRepo.existsByEmail(user.getEmail())){
            throw new UserException("L'utente " +user.getEmail() + " è già presente", HttpStatus.CONFLICT);
        }

        cartRepo.save(userCart);
        user.setCart(userCart);
        userRepo.save(user);
        return new UserDto(user.getId(),user.getFirstName(),user.getEmail());
    }

    public UserDto findUser(String email){
        User user = userUtils.findUserByEmail(email);
        return new UserDto(user.getId(), user.getFirstName(), user.getEmail());
    }

    public User modifyUser(String email, User updatedData){
        User oldUser = userUtils.findUserByEmail(email);
        BeanUtils.copyProperties(updatedData, oldUser, "id");
        return userRepo.save(oldUser);
    }

    public void deleteUser(String email){
        User user = userUtils.findUserByEmail(email);
        userRepo.delete(user);
    }



}

