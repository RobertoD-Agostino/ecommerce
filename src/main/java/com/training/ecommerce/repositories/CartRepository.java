package com.training.ecommerce.repositories;

import com.training.ecommerce.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {

}
