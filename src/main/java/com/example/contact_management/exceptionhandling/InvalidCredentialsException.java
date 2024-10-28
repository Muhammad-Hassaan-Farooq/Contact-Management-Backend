package com.example.contact_management.exceptionhandling;



public class InvalidCredentialsException extends RuntimeException{
    
    public InvalidCredentialsException(String message){
        super(message);
    }
}
