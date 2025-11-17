package com.training.ecommerce.controllers;

import com.training.ecommerce.dtos.UserDto;
import com.training.ecommerce.entities.User;
import com.training.ecommerce.repositories.UserRepository;
import com.training.ecommerce.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/createUser")
    public ResponseEntity<UserDto> createUser(@RequestBody User user){
        UserDto ret = userService.createUser(user);
        return new ResponseEntity<>(ret, HttpStatus.CREATED);
    }

    @GetMapping("/findUser")
    public ResponseEntity<UserDto> findUser(@RequestParam String email){
        UserDto ret = userService.findUser(email);
        return new ResponseEntity<>(ret, HttpStatus.FOUND);
    }

    @PutMapping("/modifyUser")
    public ResponseEntity<User> modifyUser(@RequestParam String email, @RequestBody User user){
        User ret = userService.modifyUser(email, user);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<User> modifyUser(@RequestParam String email){
        userService.deleteUser(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
