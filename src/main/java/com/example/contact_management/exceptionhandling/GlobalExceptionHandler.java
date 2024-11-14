package com.example.contact_management.exceptionhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler{
    


    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<String> invalidCredentialsHandler(Exception e){
        
        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED); 
    }

    @ExceptionHandler(value=ResourceAlreadyExistsException.class)
    public ResponseEntity<String> alreadyExistsHandler(Exception e){
        return ResponseEntity.ok(e.getMessage()); 
    }
    
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<String> notfoundHandler(Exception e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<String> unauthorizedHandler(Exception e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value=PasswordChangeException.class)
    public ResponseEntity<String> passwordChangeExceptionHandler(Exception e){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
    }

}
