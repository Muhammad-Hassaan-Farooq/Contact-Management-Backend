package com.example.contact_management.contacts.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.contact_management.auth.models.User;
import com.example.contact_management.auth.repositories.UserRepository;
import com.example.contact_management.contacts.DTO.ContactResponseDTO;
import com.example.contact_management.contacts.DTO.CreateContactDTO;
import com.example.contact_management.contacts.DTO.PaginatedContactListDTO;
import com.example.contact_management.contacts.models.Contact;
import com.example.contact_management.contacts.repositories.ContactRepository;
import com.example.contact_management.exceptionhandling.ResourceNotFoundException;
import com.example.contact_management.exceptionhandling.UnauthorizedException;


@Service
public class ContactService{

    private ContactRepository contactRepository;
    private UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository){
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    public ContactResponseDTO createContact(CreateContactDTO createContactDTO,User user){

        return ContactMapper.contactToContactResponse(
                contactRepository.save(
                    ContactMapper.createContactDTOToContact(createContactDTO,user)));
    }

    public ContactResponseDTO getContactById(Long id, User user){
        Contact contact = contactRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Contact doesnot exist") 
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

        PaginatedContactListDTO response = ContactMapper.paginatedContactsToPaginatedContactDTO(contactsPage);



        return response;

    }
}
