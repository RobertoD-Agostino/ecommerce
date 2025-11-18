package com.training.ecommerce.controllers;

import com.training.ecommerce.entities.Product;
import com.training.ecommerce.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/addProduct")
    public ResponseEntity<Product> addProduct(@RequestBody Product product){
        Product ret = productService.addProduct(product);
        return new ResponseEntity<>(ret, HttpStatus.CREATED);
    }

    @PutMapping("/modifyProduct")
    public ResponseEntity<Product> modifyProduct(@RequestParam String code, @RequestBody Product product){
        Product ret = productService.modifyProduct(code, product);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/findProduct")
    public ResponseEntity<Product> findProductByCode(@RequestParam String code){
        Product ret = productService.findProduct(code);
        return new ResponseEntity<>(ret, HttpStatus.FOUND);
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<Product> deleteProduct(@RequestParam String code){
        productService.deleteProduct(code);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}
