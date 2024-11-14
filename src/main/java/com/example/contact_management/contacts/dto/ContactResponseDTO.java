package com.example.contact_management.contacts.dto;

import java.util.List;

import com.example.contact_management.contacts.models.EmailAddress;
import com.example.contact_management.contacts.models.PhoneNumber;

public record ContactResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String title,
        List<EmailAddress> emailAddresses,
        List<PhoneNumber> phoneNumbers
        ){
    
}
