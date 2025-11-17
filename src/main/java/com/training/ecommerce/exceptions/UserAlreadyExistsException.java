package com.training.ecommerce.exceptions;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(){}

    public UserAlreadyExistsException(String message){
        super(message);
    }
}
