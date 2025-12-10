package com.training.ecommerce.repositories;

import com.training.ecommerce.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
    Optional<CartItem> findByProduct_CodeAndCart_User_Email(String code, String email);
    boolean existsByProduct_CodeAndCart_User_Email(String code, String email);
}
