package com.example.contact_management.auth.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.contact_management.auth.models.LoginRequestDTO;
import com.example.contact_management.auth.models.SignupRequestDTO;
import com.example.contact_management.auth.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController{


    private final AuthService authService;

    

    public AuthController(
                          AuthService authService
                          ){
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest req, HttpServletResponse res, @RequestBody LoginRequestDTO request) {
        
        
        authService.login(req,res,request);


        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO request) {
            
        authService.signup(request);

        return ResponseEntity.ok("User Registered succesfully");
    }

    
    @PostMapping("/logout")
    public ResponseEntity<String>  postMethodName(HttpServletRequest req, HttpServletResponse res, Authentication authentication) {
        authService.logout(req,res,authentication);
        return ResponseEntity.ok("Logged out succesfully");
    }
    

}

