package com.example.contact_management.controllertests;

import com.example.contact_management.contacts.services.ContactService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class ContactControllerTests {
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

        Mockito.when(contactService.getContactById(Mockito.anyLong(), Mockito.any())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts/1")
                .with(SecurityMockMvcRequestPostProcessors.user("test@example.com").password("password").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
