package com.example.contact_management.controllertests;


import com.example.contact_management.auth.services.AuthService;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTests {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void SuccessfulLoginTest() throws Exception{
        String loginJson = "{ \"email\": \"test@example.com\", \"password\": \"password\" }";
        Mockito.doNothing().when(authService).login(Mockito.any(),Mockito.any(),Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void IncorrectBodyToLogin() throws Exception{
        Mockito.doNothing().when(authService).login(Mockito.any(),Mockito.any(),Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void SuccessfulSignupTest() throws Exception{
        String signupJson = """
    {
        "username": "test2",
        "password": "password",
        "email": "test2@example.com",
        "firstName": "test2",
        "lastName": "user2"
    }
    """;
        Mockito.doNothing().when(authService).signup(Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    void IncorrectBodyToSignup() throws Exception{
        Mockito.doNothing().when(authService).login(Mockito.any(),Mockito.any(),Mockito.any());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    @Test
    void SuccessfulLogoutTest() throws Exception {
        // Mock authentication
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com").password("password").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Logged out succesfully"));
    }
    @Test
    void UnauthorizedLogoutTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

    }
}
