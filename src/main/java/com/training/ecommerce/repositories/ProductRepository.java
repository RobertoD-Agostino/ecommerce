package com.training.ecommerce.repositories;

import com.training.ecommerce.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>{
    Optional<Product> findByCode(String code);
    boolean existsByCode(String code);
    Optional<Product> findTopByCodeStartingWithOrderByCodeDesc(String prefix);
    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "   OR LOWER(p.category) LIKE LOWER(CONCAT('%', :filter, '%')) " +
            "   OR LOWER(p.code) LIKE LOWER(CONCAT('%', :filter, '%'))")
    Page<Product> search(@Param("filter") String filter, Pageable pageable);

}
