package com.example.contact_management.contacts.services;

import com.example.contact_management.contacts.dto.UpdateContactDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private static final String CONTACT_DOESNOT_EXIST = "Contact doesnot exist";
    private static final String NOT_HAVE_PERMISSION_TO_ACCESS_THIS_CONTACT = "You do not have permission to access this contact.";
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
            throw new UnauthorizedException(NOT_HAVE_PERMISSION_TO_ACCESS_THIS_CONTACT);
        }


        return ContactMapper.contactToContactResponse(
                contact
                );

    }

    public PaginatedContactListDTO getPaginatedContacts(int page,int size, User user){
        Pageable pageable = PageRequest.of(page,size,Sort.by("firstName").ascending().and(Sort.by("lastName").ascending()));

        Page<Contact> contactsPage = contactRepository.findByUser(user,pageable);


        return ContactMapper.paginatedContactsToPaginatedContactDTO(contactsPage);

    }

    public ContactResponseDTO updateContactById(Long id, User user, UpdateContactDTO updateContactDTO){
        Contact contact = contactRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException(CONTACT_DOESNOT_EXIST)
        );
        if (!contact.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException(NOT_HAVE_PERMISSION_TO_ACCESS_THIS_CONTACT);
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
            throw new UnauthorizedException(NOT_HAVE_PERMISSION_TO_ACCESS_THIS_CONTACT);
        }
        contactRepository.delete(contact);
    }
}
