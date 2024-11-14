package com.example.contact_management.contacts.services;

import com.example.contact_management.contacts.dto.UpdateContactDTO;
import org.springframework.data.domain.Page;

import com.example.contact_management.auth.models.User;
import com.example.contact_management.contacts.dto.ContactResponseDTO;
import com.example.contact_management.contacts.dto.CreateContactDTO;
import com.example.contact_management.contacts.dto.PaginatedContactListDTO;
import com.example.contact_management.contacts.models.Contact;

public class ContactMapper{

    private ContactMapper(){}

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

        return new ContactResponseDTO(
                contact.getId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getTitle(),
                contact.getEmailAddresses(),
                contact.getPhoneNumbers()
        );
    }

    public static PaginatedContactListDTO paginatedContactsToPaginatedContactDTO(Page<Contact> contactpage){
        
        Page<ContactResponseDTO> page = contactpage.map(ContactMapper::contactToContactResponse);

        return new PaginatedContactListDTO(
         page.getContent(),
         page.getNumber(),
         page.getTotalPages()
        );
    }

    public static Contact updateContactFromDTO(Contact contact, UpdateContactDTO updateContactDTO){
        if (updateContactDTO.firstName() != null) {
            contact.setFirstName(updateContactDTO.firstName());
        }
        if (updateContactDTO.lastName() != null) {
            contact.setLastName(updateContactDTO.lastName());
        }
        if (updateContactDTO.title() != null) {
            contact.setTitle(updateContactDTO.title());
        }
        if (updateContactDTO.emails() != null) {
            contact.getEmailAddresses().clear();
            contact.getEmailAddresses().addAll(updateContactDTO.emails());
        }
        if (updateContactDTO.phoneNumbers() != null) {
            contact.getPhoneNumbers().clear();
            contact.getPhoneNumbers().addAll(updateContactDTO.phoneNumbers());
        }

        return contact;
    }


}
