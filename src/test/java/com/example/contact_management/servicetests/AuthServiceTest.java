package com.example.contact_management.servicetests;

import com.example.contact_management.auth.models.ChangePasswordDTO;
import com.example.contact_management.auth.models.LoginRequestDTO;
import com.example.contact_management.auth.models.SignupRequestDTO;
import com.example.contact_management.auth.models.User;
import com.example.contact_management.auth.repositories.UserRepository;
import com.example.contact_management.auth.services.AuthService;
import com.example.contact_management.exceptionhandling.PasswordChangeException;
import com.example.contact_management.exceptionhandling.ResourceAlreadyExistsException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityContextRepository securityContextRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest req;

    @Mock
    private HttpServletResponse res;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("user@example.com", "password");
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(new User()));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        authService.login(req, res, loginRequest);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(securityContextRepository, times(1)).saveContext(any(), eq(req), eq(res));
    }

    @Test
    void testLogin_Failure_InvalidCredentials() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("user@example.com", "password");
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(req, res, loginRequest));

        verify(authenticationManager, never()).authenticate(any());
        verify(securityContextRepository, never()).saveContext(any(), any(), any());
    }

    @Test
    void testSignup_Success() {
        SignupRequestDTO signupRequest = new SignupRequestDTO(
                "user@example.com", "password", "John", "Doe", "john123");

        when(userRepository.findByEmail(signupRequest.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(signupRequest.password())).thenReturn("hashed_password");

        authService.signup(signupRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testSignup_Failure_UserAlreadyExists() {
        SignupRequestDTO signupRequest = new SignupRequestDTO(
                "user@example.com", "password", "John", "Doe", "john123");

        when(userRepository.findByEmail(signupRequest.email())).thenReturn(Optional.of(new User()));

        assertThrows(ResourceAlreadyExistsException.class, () -> authService.signup(signupRequest));

        verify(userRepository, never()).save(any());
    }


    @Test
    void testChangePassword_Success() {
        User user = new User();
        user.setPassword("old_hashed_password");
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("old_password", "new_password");

        when(passwordEncoder.matches(changePasswordDTO.oldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(changePasswordDTO.newPassword())).thenReturn("new_hashed_password");

        authService.changePassword(user, changePasswordDTO);

        assertEquals("new_hashed_password", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testChangePassword_Failure_IncorrectOldPassword() {
        User user = new User();
        user.setPassword("old_hashed_password");
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("wrong_password", "new_password");

        when(passwordEncoder.matches(changePasswordDTO.oldPassword(), user.getPassword())).thenReturn(false);

        assertThrows(PasswordChangeException.class, () -> authService.changePassword(user, changePasswordDTO));

        verify(userRepository, never()).save(any());
    }

    @Test
    void testChangePassword_Failure_SameAsOldPassword() {
        User user = new User();
        user.setPassword("old_hashed_password");
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("old_password", "old_password");

        when(passwordEncoder.matches(changePasswordDTO.oldPassword(), user.getPassword())).thenReturn(true);

        assertThrows(PasswordChangeException.class, () -> authService.changePassword(user, changePasswordDTO));

        verify(userRepository, never()).save(any());
    }

}
