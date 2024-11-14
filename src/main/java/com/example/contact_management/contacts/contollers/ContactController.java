package com.example.contact_management.contacts.contollers;

import com.example.contact_management.contacts.dto.UpdateContactDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.contact_management.auth.models.User;
import com.example.contact_management.contacts.dto.ContactResponseDTO;
import com.example.contact_management.contacts.dto.CreateContactDTO;
import com.example.contact_management.contacts.dto.PaginatedContactListDTO;
import com.example.contact_management.contacts.services.ContactService;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("")
    public ResponseEntity<ContactResponseDTO> createContact(@RequestBody CreateContactDTO createContactDTO, @AuthenticationPrincipal User user) {
        ContactResponseDTO createdContact = contactService.createContact(createContactDTO, user);
        return new ResponseEntity<>(createdContact, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> getContactById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        ContactResponseDTO cDto = contactService.getContactById(id, user);
        return new ResponseEntity<>(cDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<PaginatedContactListDTO> getContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        PaginatedContactListDTO pDto = contactService.getPaginatedContacts(page, size, user);
        return new ResponseEntity<>(pDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> updateContactById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody UpdateContactDTO updateContactDTO) {

        ContactResponseDTO contactResponseDTO = contactService.updateContactById(id,user,updateContactDTO);
        return new ResponseEntity<>(contactResponseDTO,HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContactById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        contactService.deleteContactById(id,user);
        return new ResponseEntity<>("Contact deleted successfully",HttpStatus.OK);

    }
    @GetMapping("/search")
    public PaginatedContactListDTO searchContacts(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String query,
            @AuthenticationPrincipal User user
    ) {
        return contactService.search(page, size, query, user);
    }



}
