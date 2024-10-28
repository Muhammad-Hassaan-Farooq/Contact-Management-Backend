package com.example.contact_management.exceptionhandling;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler{
    


    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<String> invalidCredentialsHandler(Exception e){
        
        return ResponseEntity.ok(e.getMessage());
    }

    @ExceptionHandler(value=ResourceAlreadyExistsException.class)
    public ResponseEntity<String> alreadyExistsHandler(Exception e){
        return ResponseEntity.ok(e.getMessage()); 
    }
}
