package com.example.contact_management.auth.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.contact_management.auth.repositories.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration{
    
    private final UserRepository userRepository;

    public SecurityConfiguration(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
            .csrf(AbstractHttpConfigurer::disable
                )
            .sessionManagement(session ->session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1)
                )
            .authorizeHttpRequests(request -> request
                    .requestMatchers("api/auth/login","api/auth/signup").permitAll()
                    .requestMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                    .anyRequest().authenticated()
                )
            .formLogin(AbstractHttpConfigurer::disable
                )
            .httpBasic(AbstractHttpConfigurer::disable
                )
            .addFilterBefore(new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                        throws ServletException, IOException {
                    logger.info("Processing request: " + request.getRequestURI());
                   

                    // Log the cookies
                if (request.getCookies() != null) {
                    for (Cookie cookie : request.getCookies()) {
                        logger.info("Cookie Name: " + cookie.getName() + ", Cookie Value: " + cookie.getValue());
                    }
                } else {
                    logger.info("No cookies present in the request.");
                }

                // Log the session ID
                HttpSession session = request.getSession(false);
                if (session != null) {
                    logger.info("Session ID: " + session.getId());
                } else {
                    logger.info("No session found for this request.");
                }

                // Log the authentication details
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null) {
                    logger.info("Authentication object: " + auth);
                    logger.info("Is user authenticated: " + auth.isAuthenticated());
                } else {
                    logger.info("No authentication present in the SecurityContext.");
                }

                    filterChain.doFilter(request, response);
                }
            }, UsernamePasswordAuthenticationFilter.class);

        return http.build();
            

    }

    @Bean
    UserDetailsService userDetailsService(){
        return email -> userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
}
