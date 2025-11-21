package com.training.ecommerce.services;

import com.training.ecommerce.entities.Cart;
import com.training.ecommerce.entities.CartItem;
import com.training.ecommerce.entities.Product;
import com.training.ecommerce.entities.User;
import com.training.ecommerce.exceptions.CartException;
import com.training.ecommerce.repositories.CartItemRepository;
import com.training.ecommerce.repositories.CartRepository;
import com.training.ecommerce.repositories.ProductRepository;
import com.training.ecommerce.utils.ProductUtils;
import com.training.ecommerce.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ProductRepository productRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductUtils productUtils;
    private final UserUtils userUtils;

    public CartItem addProductToCart(String code, int quantity, String email) throws RuntimeException{
        Product product = productUtils.findProductByCode(code);
        User user = userUtils.findUserByEmail(email);
        Cart userCart = user.getCart();

        if(product.getStockQuantity()-quantity<0){
            throw new CartException("La quantità selezionata è maggiore della quantità in magazzino", HttpStatus.BAD_REQUEST);
        }

        List<CartItem> cartItemList = userCart.getCartItemList();

        CartItem cartItemToUpdate = cartItemList.stream()
                .filter(cI -> cI.getProduct().getCode().equals(code))
                .findFirst()
                .orElse(null);

        int totalQuantity = cartItemToUpdate.getQuantity() + quantity;

        if(cartItemToUpdate!=null){
            if(totalQuantity<=cartItemToUpdate.getProduct().getStockQuantity()){
                cartItemToUpdate.setQuantity(totalQuantity);
                return cartItemRepo.save(cartItemToUpdate);
            }else{
                throw new CartException("La quantità selezionata è troppo grande", HttpStatus.BAD_REQUEST);
            }
        }
        return cartItemRepo.save(new CartItem(quantity,userCart,product));
    }
}
