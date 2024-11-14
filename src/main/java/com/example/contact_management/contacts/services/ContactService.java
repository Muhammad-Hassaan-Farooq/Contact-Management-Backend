package com.example.contact_management.contacts.services;

import com.example.contact_management.contacts.dto.UpdateContactDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.contact_management.auth.models.User;
import com.example.contact_management.contacts.dto.ContactResponseDTO;
import com.example.contact_management.contacts.dto.CreateContactDTO;
import com.example.contact_management.contacts.dto.PaginatedContactListDTO;
import com.example.contact_management.contacts.models.Contact;
import com.example.contact_management.contacts.repositories.ContactRepository;
import com.example.contact_management.exceptionhandling.ResourceNotFoundException;
import com.example.contact_management.exceptionhandling.UnauthorizedException;


@Service
public class ContactService{

    private final String CONTACT_DOESNOT_EXIST = "Contact doesnot exist";
    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository){
        this.contactRepository = contactRepository;
    }

    public ContactResponseDTO createContact(CreateContactDTO createContactDTO,User user){

        return ContactMapper.contactToContactResponse(
                contactRepository.save(
                    ContactMapper.createContactDTOToContact(createContactDTO,user)));
    }

    public ContactResponseDTO getContactById(Long id, User user){
        Contact contact = contactRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(CONTACT_DOESNOT_EXIST)
                );

        if (!contact.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to access this contact.");
        }


        return ContactMapper.contactToContactResponse(
                contact
                );

    }

    public PaginatedContactListDTO getPaginatedContacts(int page,int size, User user){
        Pageable pageable = PageRequest.of(page,size);

        Page<Contact> contactsPage = contactRepository.findByUser(user,pageable);


        return ContactMapper.paginatedContactsToPaginatedContactDTO(contactsPage);

    }

    public ContactResponseDTO updateContactById(Long id, User user, UpdateContactDTO updateContactDTO){
        Contact contact = contactRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(CONTACT_DOESNOT_EXIST)
        );
        if (!contact.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to access this contact.");
        }

        Contact updatedContact = ContactMapper.updateContactFromDTO(contact,updateContactDTO);
        contactRepository.save(updatedContact);
        return ContactMapper.contactToContactResponse(contact);

    }

    public void deleteContactById(Long id, User user){
        Contact contact = contactRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(CONTACT_DOESNOT_EXIST)
        );

        if (!contact.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to access this contact.");
        }
        contactRepository.delete(contact);
    }
}
