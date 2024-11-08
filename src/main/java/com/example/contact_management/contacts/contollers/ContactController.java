package com.example.contact_management.contacts.contollers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.contact_management.auth.models.User;
import com.example.contact_management.contacts.DTO.ContactResponseDTO;
import com.example.contact_management.contacts.DTO.CreateContactDTO;
import com.example.contact_management.contacts.DTO.PaginatedContactListDTO;
import com.example.contact_management.contacts.models.Contact;
import com.example.contact_management.contacts.services.ContactService;

@RestController
@RequestMapping("/api/contacts")
public class ContactController{
    
    private ContactService contactService;

    public ContactController(ContactService contactService){
        this.contactService = contactService;
    }
    @PostMapping("") 
    public ResponseEntity<ContactResponseDTO> createContact(@RequestBody CreateContactDTO createContactDTO, @AuthenticationPrincipal User user){
        ContactResponseDTO createdContact = contactService.createContact(createContactDTO,user);
        return new ResponseEntity<ContactResponseDTO>(createdContact,HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> getContactById(@PathVariable Long id,  @AuthenticationPrincipal User user){
       ContactResponseDTO cDto =  contactService.getContactById(id,user);
       return new ResponseEntity<ContactResponseDTO>(cDto,HttpStatus.OK);
    }
    
    @GetMapping("")
    public ResponseEntity<PaginatedContactListDTO> getContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user){
        PaginatedContactListDTO pDto = contactService.getPaginatedContacts(page,size,user);
        return new ResponseEntity<>(pDto,HttpStatus.OK);
    }

}
