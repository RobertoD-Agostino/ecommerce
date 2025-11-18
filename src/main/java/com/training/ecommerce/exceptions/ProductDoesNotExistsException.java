package com.training.ecommerce.exceptions;

public class ProductDoesNotExistsException extends RuntimeException{
    public ProductDoesNotExistsException(){}

    public ProductDoesNotExistsException(String message){
        super(message);
    }
}
