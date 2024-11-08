package com.example.contact_management.exceptionhandling;


public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message){
        super(message);
    }
}
