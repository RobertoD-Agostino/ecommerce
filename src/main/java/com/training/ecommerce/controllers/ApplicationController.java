package com.training.ecommerce.controllers;

import com.training.ecommerce.entities.CartItem;
import com.training.ecommerce.services.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/addProductToCart")
    public ResponseEntity addProductToCart(@RequestParam String code,@RequestParam int quantity,@RequestParam String email){
        CartItem ret = applicationService.addProductToCart(code,quantity,email);
        return new ResponseEntity(ret,HttpStatus.OK);
    }
}
