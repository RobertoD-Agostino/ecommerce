package com.training.ecommerce.repositories;

import com.training.ecommerce.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>{
    Optional<Product> findByCode(String code);
    boolean existsByCode(String code);
}
