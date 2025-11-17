package com.training.ecommerce.exceptions;


public class UserDoesNotExistsException extends RuntimeException{
    public UserDoesNotExistsException(){}

    public UserDoesNotExistsException(String message){
        super(message);
    }
}
