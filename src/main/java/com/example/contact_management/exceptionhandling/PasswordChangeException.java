package com.example.contact_management.exceptionhandling;

public class PasswordChangeException extends RuntimeException{
    public PasswordChangeException(String msg){
        super(msg);
    }
}
