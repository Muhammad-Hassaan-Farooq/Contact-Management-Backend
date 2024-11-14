package com.example.contact_management.servicetests;

import com.example.contact_management.auth.models.User;
import com.example.contact_management.contacts.dto.ContactResponseDTO;
import com.example.contact_management.contacts.dto.CreateContactDTO;
import com.example.contact_management.contacts.dto.PaginatedContactListDTO;
import com.example.contact_management.contacts.dto.UpdateContactDTO;
import com.example.contact_management.contacts.models.Contact;
import com.example.contact_management.contacts.repositories.ContactRepository;
import com.example.contact_management.contacts.services.ContactService;
import com.example.contact_management.exceptionhandling.ResourceNotFoundException;
import com.example.contact_management.exceptionhandling.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @Mock
    private User user;

    @BeforeEach
    public void setUp() {

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createContact_Success() {
        CreateContactDTO createContactDTO = new CreateContactDTO(
                "John",
                "Doe",
                "Software",
                List.of(),
                List.of()
        );

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setTitle("Software");


        when(contactRepository.save(any(Contact.class))).thenReturn(contact);

        ContactResponseDTO response = contactService.createContact(createContactDTO, user);

        assertNotNull(response);
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        assertEquals("Software", response.title());
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void getContactById_Success() {

        Long contactId = 1L;
        Contact contact = new Contact();
        contact.setId(contactId);
        contact.setUser(user);
        contact.setFirstName("John");
        contact.setLastName("Doe");

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        ContactResponseDTO response = contactService.getContactById(contactId, user);

        assertNotNull(response);
        assertEquals("John", response.firstName());
        assertEquals("Doe", response.lastName());
        verify(contactRepository, times(1)).findById(contactId);
    }

    @Test
    void getContactById_ResourceNotFoundException() {
        // Arrange
        Long contactId = 1L;

        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            contactService.getContactById(contactId, user);
        });
    }
    @Test
    void getContactById_UnauthorizedException() {

        Long contactId = 1L;
        User user1 = new User();
        user1.setId(2L);

        Contact contact = new Contact();
        contact.setId(contactId);
        contact.setUser(new User());
        contact.getUser().setId(1L);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        assertThrows(UnauthorizedException.class, () -> {
            contactService.getContactById(contactId, user1);
        });

        verify(contactRepository, times(1)).findById(contactId);
    }

    @Test
    void getPaginatedContacts_Success() {

        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending().and(Sort.by("lastName").ascending()));
        Page<Contact> contactsPage = new PageImpl<>(List.of(new Contact(), new Contact()));

        when(contactRepository.findByUser(user, pageable)).thenReturn(contactsPage);

        PaginatedContactListDTO response = contactService.getPaginatedContacts(page, size, user);

        assertNotNull(response);
        verify(contactRepository, times(1)).findByUser(user, pageable);
    }

    @Test
    void updateContactById_Success() {

        Long contactId = 1L;
        UpdateContactDTO updateContactDTO = new UpdateContactDTO("Updated", "Name", "New Title", List.of(), List.of());
        Contact existingContact = new Contact();
        existingContact.setId(contactId);
        existingContact.setUser(user);
        existingContact.setFirstName("Old");
        existingContact.setLastName("Name");

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(existingContact);


        ContactResponseDTO response = contactService.updateContactById(contactId, user, updateContactDTO);


        assertNotNull(response);
        assertEquals("Updated", response.firstName());
        assertEquals("Name", response.lastName());
        verify(contactRepository, times(1)).findById(contactId);
        verify(contactRepository, times(1)).save(any(Contact.class));
    }

    @Test
    void updateContactById_ResourceNotFoundException() {

        Long contactId = 1L;
        UpdateContactDTO updateContactDTO = new UpdateContactDTO("Updated", "Name", "New Title", List.of(), List.of());

        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            contactService.updateContactById(contactId, user, updateContactDTO);
        });
    }



}
