package com.example.contact_management.contacts.dto;

import java.util.List;


public record PaginatedContactListDTO(
        List<ContactResponseDTO> contacts,
        int currentPage,
        int totalPages
        ){}
