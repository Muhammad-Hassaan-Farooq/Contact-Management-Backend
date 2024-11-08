package com.example.contact_management.contacts.DTO;

import java.util.List;


public record PaginatedContactListDTO(
        List<ContactResponseDTO> contacts,
        int currentPage,
        int totalPages
        ){}
