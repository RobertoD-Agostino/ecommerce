package com.training.ecommerce.services;

import com.training.ecommerce.entities.Product;
import com.training.ecommerce.exceptions.ProductException;
import com.training.ecommerce.repositories.ProductRepository;
import com.training.ecommerce.utils.ProductUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;
    private final ProductUtils productUtils;

    public Product addProduct(Product product)throws RuntimeException{
        if(productRepo.existsByCode(product.getCode())){
            throw new ProductException("Il prodotto " + product.getName() + " è già esistente", HttpStatus.CONFLICT);
        }
        return productRepo.save(product);
    }

    public Product modifyProduct(String code, Product newData){
        Product oldData = productUtils.findProductByCode(code);
        BeanUtils.copyProperties(newData, oldData, "id");
        return productRepo.save(oldData);
    }

    public Product findProduct(String code){
        return productUtils.findProductByCode(code);
    }

    public void deleteProduct(String code){
        Product product = productUtils.findProductByCode(code);
        productRepo.delete(product);
    }
}
