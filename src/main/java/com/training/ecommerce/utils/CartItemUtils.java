package com.training.ecommerce.utils;

import com.training.ecommerce.entities.CartItem;
import com.training.ecommerce.exceptions.ProductException;
import com.training.ecommerce.repositories.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartItemUtils {

    private final CartItemRepository cartItemRepo;

    public CartItem findCartItemByProductCodeAndUserEmail(String code, String email)throws RuntimeException{
        return cartItemRepo.findByProduct_CodeAndCart_User_Email(code, email)
                .orElseThrow(() -> new ProductException("Prodotto con codice " + code + " non trovato", HttpStatus.NOT_FOUND));
    }
}
