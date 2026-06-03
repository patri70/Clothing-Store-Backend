package ro.ubbcluj.assignment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ro.ubbcluj.assignment.dto.LoginRequest;
import ro.ubbcluj.assignment.dto.RegisterRequest;
import ro.ubbcluj.assignment.dto.UserResponse;
import ro.ubbcluj.assignment.security.JwtUtils;
import ro.ubbcluj.assignment.security.UserDetailsImpl;
import ro.ubbcluj.assignment.service.AuthService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtils = mock(JwtUtils.class);
        authController = new AuthController(authService, authenticationManager, jwtUtils);
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("user", "pass");
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L, "user", "email", "pass", 
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.getAuthorities()).thenAnswer(i -> userDetails.getAuthorities());
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mock-jwt");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("user", "pass", "email");
        UserResponse userResponse = new UserResponse(1L, "user", "email", "ROLE_USER", Collections.emptySet());
        
        when(authService.register(any(RegisterRequest.class))).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = authController.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("user", response.getBody().username());
    }
}
