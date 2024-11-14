package com.example.contact_management.controllertests;

import com.example.contact_management.contacts.dto.ContactResponseDTO;
import com.example.contact_management.contacts.dto.PaginatedContactListDTO;
import com.example.contact_management.contacts.dto.UpdateContactDTO;
import com.example.contact_management.contacts.models.EmailAddress;
import com.example.contact_management.contacts.models.PhoneNumber;
import com.example.contact_management.contacts.services.ContactService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ContactControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;


    @Test
    void createContactUnauthorized() throws Exception {
        String createContactJson = """
            {
                "username": "test2",
                "password": "password",
                "email": "test2@example.com",
                "firstName": "test2",
                "lastName": "user2"
            }
            """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createContactJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void getContactByIdUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getContactsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts"))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateContactByIdUnauthorized() throws Exception {
        String updateContactJson = """
            {
                "firstName": "updatedName"
            }
            """;
        mockMvc.perform(MockMvcRequestBuilders.put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateContactJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteContactByIdUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/contacts/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void searchContactsUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/search")
                        .param("page", "0")
                        .param("size", "10")
                        .param("query", "test"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getContactByIdAuthorized() throws Exception {

        Mockito.when(contactService.getContactById(anyLong(), any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/1")
                .with(SecurityMockMvcRequestPostProcessors.user("test@example.com").password("password").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void getContactsAuthorized() throws Exception {
        Mockito.when(contactService.getPaginatedContacts(Mockito.anyInt(), Mockito.anyInt(), any()))
                .thenReturn(new PaginatedContactListDTO(
                        List.of(),
                        0,0
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void updateContactByIdAuthorized() throws Exception {
        Mockito.when(contactService.updateContactById(anyLong(), any(), any(UpdateContactDTO.class)))
                .thenReturn(new ContactResponseDTO(
                        1L,
                        "test",
                        "user",
                        "software",
                        List.of(),
                        List.of()
                ));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\", \"lastName\":\"Doe\"}"))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void deleteContactByIdAuthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Contact deleted successfully"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void searchContactsAuthorized() throws Exception {
        Mockito.when(contactService.search(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.any()))
                .thenReturn(new PaginatedContactListDTO(
                      List.of(),
                      0,
                        0
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/search")
                        .param("page", "0")
                        .param("size", "10")
                        .param("query", "John")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void exportContactsAuthorized() throws Exception {
        Mockito.when(contactService.getPaginatedContacts(Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(new PaginatedContactListDTO(
                        List.of(),
                        0,
                        0
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/export")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void importContactsAuthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/contacts/import")
                        .file("file", "dummy vcf content".getBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void exportContactsSuccessWithContacts() throws Exception {
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setEmail("test@example.com");
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setLabel("Home");
        phoneNumber.setNumber("1234567890");
        List<EmailAddress> emails = List.of(emailAddress);
        List<PhoneNumber> phones = List.of(phoneNumber);
        ContactResponseDTO contact = new ContactResponseDTO(1L, "John", "Doe", "Mr.", emails, phones);
        PaginatedContactListDTO paginatedContactList = new PaginatedContactListDTO(List.of(contact), 1, 1);

        Mockito.when(contactService.getPaginatedContacts(anyInt(), anyInt(), any())).thenReturn(paginatedContactList);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/export")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertEquals("attachment; filename=contacts.vcf", response.getHeader(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals("text/vcard", response.getContentType());
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void exportContactsInternalServerError() throws Exception {
        Mockito.when(contactService.getPaginatedContacts(anyInt(), anyInt(), any())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/export")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
    @Test
    @WithMockUser(username = "test@example.com", roles = {"USER"})
    void importContactsSuccess() throws Exception {
        // Mock file content with valid VCard data
        String vcardContent = "BEGIN:VCARD\nFN:John Doe\nTEL;TYPE=HOME:1234567890\nEMAIL:john.doe@example.com\nEND:VCARD";
        MockMultipartFile file = new MockMultipartFile("file", "contacts.vcf", "text/vcard", vcardContent.getBytes());

        Mockito.when(contactService.createContact(any(),any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/contacts/import")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("Imported successfully"));
    }



}
