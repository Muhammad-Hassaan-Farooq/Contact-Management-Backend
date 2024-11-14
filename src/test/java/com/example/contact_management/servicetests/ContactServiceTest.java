package com.example.contact_management.servicetests;

import com.example.contact_management.auth.models.User;
import com.example.contact_management.contacts.dto.ContactResponseDTO;
import com.example.contact_management.contacts.dto.CreateContactDTO;
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
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContactServiceTest {

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
        // Arrange
        Long contactId = 1L;
        Contact contact = new Contact();
        contact.setId(contactId);
        contact.setUser(new User());
        contact.setFirstName("John");
        contact.setLastName("Doe");

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            contactService.getContactById(contactId, user);
        });
    }
}
