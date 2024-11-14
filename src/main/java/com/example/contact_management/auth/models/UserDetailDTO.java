package com.example.contact_management.auth.models;


import java.time.LocalDateTime;

public record UserDetailDTO (
        String firstName,
        String lastName,
        String username,
        String email,
        LocalDateTime createdAt){
}
