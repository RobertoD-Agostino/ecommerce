package com.training.ecommerce.controllers;

import com.training.ecommerce.dtos.CartItemDto;
import com.training.ecommerce.entities.CartItem;
import com.training.ecommerce.services.ApplicationService;
import com.training.ecommerce.utils.CartItemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CartItemUtils cartItemUtils;

    @PostMapping("/addProductToCart")
    public ResponseEntity addProductToCart(@RequestParam String code,@RequestParam int quantity,@RequestParam String email){
        CartItem ret = applicationService.addProductToCart(code,quantity,email);
        return new ResponseEntity(ret,HttpStatus.OK);
    }

    @GetMapping("/findCartItem")
    public ResponseEntity<CartItem> findCartItemByProductCode(@RequestParam String code,@RequestParam String email){
        CartItem cartItem = cartItemUtils.findCartItemByProductCodeAndUserEmail(code,email);
        return new ResponseEntity(cartItem, HttpStatus.FOUND);
    }

    @PutMapping("/reduceCartItemQuantity")
    public ResponseEntity reduceCartItemQuantity(@RequestParam String code,@RequestParam int quantity,@RequestParam String email){
        CartItemDto ret = applicationService.reduceQuantityProductFromCart(code,quantity,email);
        return new ResponseEntity(ret, HttpStatus.OK);
    }

    @DeleteMapping("/removeCartItem")
    public ResponseEntity deleteCartItem(@RequestParam String code, @RequestParam String email){
        applicationService.deleteCartItem(code,email);
        return ResponseEntity.ok("Il prodotto è stato eliminato con successo");
    }
}
