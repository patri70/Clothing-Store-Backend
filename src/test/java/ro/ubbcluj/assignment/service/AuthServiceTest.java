package ro.ubbcluj.assignment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import ro.ubbcluj.assignment.dto.RegisterRequest;
import ro.ubbcluj.assignment.dto.UserResponse;
import ro.ubbcluj.assignment.model.Role;
import ro.ubbcluj.assignment.model.User;
import ro.ubbcluj.assignment.repository.RoleRepository;
import ro.ubbcluj.assignment.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authService = new AuthService(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("test", "pass", "test@test.com");
        Role role = new Role("ROLE_USER");
        
        when(userRepository.existsByUsername("test")).thenReturn(false);
        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        UserResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("test", response.username());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterAlreadyExists() {
        RegisterRequest request = new RegisterRequest("test", "pass", "test@test.com");
        when(userRepository.existsByUsername("test")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(request));
    }
}
