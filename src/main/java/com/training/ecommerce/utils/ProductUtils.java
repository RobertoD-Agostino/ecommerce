package com.training.ecommerce.utils;

import com.training.ecommerce.entities.Product;
import com.training.ecommerce.exceptions.ProductException;
import com.training.ecommerce.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductUtils {

    private final ProductRepository productRepo;

    public Product findProductByCode(String code)throws RuntimeException{
        return productRepo.findByCode(code)
                .orElseThrow(() -> new ProductException("Prodotto con codice " + code + " non trovato", HttpStatus.NOT_FOUND));
    }
}
