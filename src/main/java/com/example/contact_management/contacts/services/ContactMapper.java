package com.example.contact_management.contacts.services;

import org.springframework.data.domain.Page;

import com.example.contact_management.auth.models.User;
import com.example.contact_management.contacts.DTO.ContactResponseDTO;
import com.example.contact_management.contacts.DTO.CreateContactDTO;
import com.example.contact_management.contacts.DTO.PaginatedContactListDTO;
import com.example.contact_management.contacts.models.Contact;

public class ContactMapper{

    public static Contact createContactDTOToContact(CreateContactDTO createContactDTO,User user){
        Contact contact = new Contact();

        contact.setTitle(createContactDTO.title());
        contact.setFirstName(createContactDTO.firstName());
        contact.setLastName(createContactDTO.lastName());
        contact.setPhoneNumbers(createContactDTO.phoneNumbers());
        contact.setEmailAddresses(createContactDTO.emailAddresses());
        contact.setUser(user);

        return contact;
    }

    public static ContactResponseDTO contactToContactResponse(Contact contact){
        ContactResponseDTO cDto = new ContactResponseDTO(
            contact.getId(),
            contact.getFirstName(),
            contact.getLastName(),
            contact.getTitle(),
            contact.getEmailAddresses(),
            contact.getPhoneNumbers()
                );

        return cDto;
    }

    public static PaginatedContactListDTO paginatedContactsToPaginatedContactDTO(Page<Contact> contactpage){
        
        Page<ContactResponseDTO> page = contactpage.map(ContactMapper::contactToContactResponse);

       PaginatedContactListDTO paginatedContactListDTO = new PaginatedContactListDTO(
        page.getContent(),
        page.getNumber(),
        page.getTotalPages()
       );

       return paginatedContactListDTO;
    }


}
