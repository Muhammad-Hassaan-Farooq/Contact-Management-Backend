package com.example.contact_management.contacts.dto;

import com.example.contact_management.contacts.models.EmailAddress;
import com.example.contact_management.contacts.models.PhoneNumber;

import java.util.List;

public record UpdateContactDTO(
        String firstName,
        String lastName,
        String title,
        List<EmailAddress> emails,
        List<PhoneNumber> phoneNumbers
) {
}
