
package com.example.contact_management.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    
    @Bean
    WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") // Allows requests from this origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Allows specified HTTP methods
                        .allowedHeaders("*") // Allows all headers
                        .allowCredentials(true); // Allows credentials (e.g., cookies)
            }
        };
    }
}
