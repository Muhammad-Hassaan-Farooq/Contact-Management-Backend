package com.example.contact_management.auth.services;

import com.example.contact_management.auth.models.*;
import com.example.contact_management.exceptionhandling.PasswordChangeException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import com.example.contact_management.auth.repositories.UserRepository;
import com.example.contact_management.exceptionhandling.ResourceAlreadyExistsException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Service
public class AuthService{

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final UserRepository userRepository;   
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager, 
            SecurityContextRepository securityContextRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
            ){

        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
            }

    public void login(HttpServletRequest req,HttpServletResponse res, LoginRequestDTO data){
        
       if(userRepository.findByEmail(data.email()).isEmpty()){
            throw new BadCredentialsException("Invalid username or password"); 
        }
        
    

        Authentication authentication =  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    data.email(),
                    data.password()
                    )
                );

        

        

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();

        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context,req,res);
    }

    public void signup(SignupRequestDTO data){

        if(userRepository.findByEmail(data.email()).isPresent()){
            throw new ResourceAlreadyExistsException("User already exists"); 
        }

        User user = new User();
        user.setEmail(data.email());
        user.setPassword(passwordEncoder.encode(data.password()));
        user.setUsername(data.username());
        user.setFirstName(data.firstName());
        user.setLastName(data.lastName());

        userRepository.save(user);

    }

    public void logout(HttpServletRequest req, HttpServletResponse res, Authentication authentication){
        
        SecurityContextLogoutHandler sHandler = new SecurityContextLogoutHandler();
        sHandler.logout(req,res,authentication);
    
    }

    public void changePassword(User user, ChangePasswordDTO changePasswordDTO){
        if (!passwordEncoder.matches(changePasswordDTO.oldPassword(), user.getPassword())) {
            throw new PasswordChangeException("Old password is incorrect");
        }
        if (changePasswordDTO.oldPassword().equals(changePasswordDTO.newPassword())) {
            throw new PasswordChangeException("New password cannot be the same as the old password");
        }

        String hashedNewPassword = passwordEncoder.encode(changePasswordDTO.newPassword());
        user.setPassword(hashedNewPassword);

        userRepository.save(user);
    }

}


