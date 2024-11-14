package com.example.contact_management.auth.models;

public record ChangePasswordDTO(
        String oldPassword,
        String newPassword
) {
}
