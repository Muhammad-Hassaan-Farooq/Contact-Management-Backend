package com.example.contact_management.controllertests;


import com.example.contact_management.auth.controllers.AuthController;
import com.example.contact_management.auth.services.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(controllers = {AuthController.class} ,excludeAutoConfiguration = {SecurityAutoConfiguration.class})
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
        String signupJson = "{\n" +
                "    \"username\": \"test2\",\n" +
                "    \"password\": \"password\",\n" +
                "    \"email\": \"test2@example.com\",\n" +
                "    \"firstName\": \"test2\",\n" +
                "    \"lastName\": \"user2\"\n" +
                "}";
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
}
