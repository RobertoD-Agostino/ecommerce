package com.training.ecommerce.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.training.ecommerce.entities.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemDto {

    private String message;
    private CartItem cartItem;

    public CartItemDto(CartItem cartItem){
        this.cartItem = cartItem;
    }

    public CartItemDto(String message){
        this.message = message;
    }

}
