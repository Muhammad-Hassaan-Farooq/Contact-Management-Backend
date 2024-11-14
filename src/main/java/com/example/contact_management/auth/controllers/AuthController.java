package com.example.contact_management.auth.controllers;

import com.example.contact_management.auth.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

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

    @GetMapping("/profile")
    public ResponseEntity<UserDetailDTO> getUserDetails(@AuthenticationPrincipal User user){
        return new ResponseEntity<>(new UserDetailDTO(
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        ),HttpStatus.OK);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal User user, @RequestBody ChangePasswordDTO changePasswordDTO){
        authService.changePassword(user,changePasswordDTO);
        return new ResponseEntity<>("Password changed",HttpStatus.OK);
    }
    

}

