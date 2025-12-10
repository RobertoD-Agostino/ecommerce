package com.training.ecommerce.controllers;

import com.training.ecommerce.dtos.CartItemDto;
import com.training.ecommerce.entities.Cart;
import com.training.ecommerce.entities.CartItem;
import com.training.ecommerce.services.CartService;
import com.training.ecommerce.utils.CartItemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartItemUtils cartItemUtils;

    @PostMapping("/addProductToCart")
    public ResponseEntity addProductToCart(@RequestParam String code,@RequestParam int quantity,@RequestParam String email){
        CartItemDto ret = cartService.addProductToCart(code,quantity,email);
        return new ResponseEntity(ret,HttpStatus.OK);
    }

    @GetMapping("/findCartItem")
    public ResponseEntity<CartItem> findCartItemByProductCode(@RequestParam String code,@RequestParam String email){
        CartItem cartItem = cartItemUtils.findCartItemByProductCodeAndUserEmail(code,email);
        return new ResponseEntity(cartItem, HttpStatus.FOUND);
    }

    @PutMapping("/modifyCartItemQuantity")
    public ResponseEntity modifyCartItemQuantity(@RequestParam String code,@RequestParam int quantity,@RequestParam String email){
        CartItemDto ret = cartService.modifyQuantityProductFromCart(code,quantity,email);
        return new ResponseEntity(ret, HttpStatus.OK);
    }

    @DeleteMapping("/removeCartItem")
    public ResponseEntity deleteCartItem(@RequestParam String code, @RequestParam String email){
        cartService.deleteCartItem(code,email);
        return ResponseEntity.ok("Il prodotto è stato eliminato con successo");
    }

    @GetMapping("/getCart")
    public ResponseEntity getCart(@RequestParam String email){
        Cart ret = cartService.getCart(email);
        return new ResponseEntity(ret, HttpStatus.OK);
    }
}
