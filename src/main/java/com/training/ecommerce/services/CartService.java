package com.training.ecommerce.services;

import com.training.ecommerce.dtos.CartItemDto;
import com.training.ecommerce.entities.Cart;
import com.training.ecommerce.entities.CartItem;
import com.training.ecommerce.entities.Product;
import com.training.ecommerce.entities.User;
import com.training.ecommerce.exceptions.CartException;
import com.training.ecommerce.repositories.CartItemRepository;
import com.training.ecommerce.utils.CartItemUtils;
import com.training.ecommerce.utils.ProductUtils;
import com.training.ecommerce.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepo;
    private final ProductUtils productUtils;
    private final CartItemUtils cartItemUtils;
    private final UserUtils userUtils;

    public CartItem addProductToCart(String code, int quantity, String email) {
        Product product = productUtils.findProductByCode(code);
        User user = userUtils.findUserByEmail(email);
        Cart userCart = user.getCart();

        if (product.getStockQuantity() - quantity < 0) {
            throw new CartException("La quantità selezionata è maggiore della quantità in magazzino",HttpStatus.BAD_REQUEST);
        } else if (quantity<=0) {
            throw new CartException("La quantità selezionata deve essere almeno di 1",HttpStatus.BAD_REQUEST);
        }

        Optional<CartItem> optionalCartItem =
                cartItemRepo.findByProduct_CodeAndCart_User_Email(code, email);

        return optionalCartItem
                .map(cartItem -> {
                    int totalQuantity = cartItem.getQuantity() + quantity;

                    if (totalQuantity > product.getStockQuantity()) {
                        throw new CartException("La quantità selezionata è troppo grande",
                                HttpStatus.BAD_REQUEST);
                    }

                    cartItem.setQuantity(totalQuantity);
                    return cartItemRepo.save(cartItem);
                })
                .orElseGet(() -> cartItemRepo.save(new CartItem(quantity, userCart, product)));
    }

    public CartItemDto modifyQuantityProductFromCart(String code, int quantity, String email){
        CartItem cartItem = cartItemUtils.findCartItemByProductCodeAndUserEmail(code, email);
        int totalQuantity = cartItem.getQuantity()-quantity;

        if(totalQuantity<0){
            throw new CartException("La quantità selezionata è troppo grande", HttpStatus.BAD_REQUEST);
        }else if (quantity<=0) {
            throw new CartException("La quantità deve essere almeno di 1",HttpStatus.BAD_REQUEST);
        }else if (totalQuantity==0) {
            deleteCartItem(code,email);
            cartItemRepo.delete(cartItem);
            return new CartItemDto("Il prodotto è stato eliminato con successo!");
        }else{
            cartItem.setQuantity(totalQuantity);
            cartItemRepo.save(cartItem);
            return new CartItemDto(cartItem);
        }
    }

    public void deleteCartItem(String code, String email){
        CartItem cartItem = cartItemUtils.findCartItemByProductCodeAndUserEmail(code, email);
        cartItemRepo.delete(cartItem);
    }





}
