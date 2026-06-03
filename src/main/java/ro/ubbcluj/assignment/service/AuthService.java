package ro.ubbcluj.assignment.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.ubbcluj.assignment.dto.LoginRequest;
import ro.ubbcluj.assignment.dto.RegisterRequest;
import ro.ubbcluj.assignment.dto.UserResponse;
import ro.ubbcluj.assignment.model.Permission;
import ro.ubbcluj.assignment.model.Role;
import ro.ubbcluj.assignment.model.User;
import ro.ubbcluj.assignment.repository.RoleRepository;
import ro.ubbcluj.assignment.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                userRole
        );
        User savedUser = userRepository.save(user);

        return toResponse(savedUser);
    }

    @Transactional
    public String generateResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String token = java.util.UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Mock Email: In a real app, send this via email.
        System.out.println("--------------------------------------------------");
        System.out.println("PASSWORD RESET REQUEST for " + email);
        System.out.println("Token: " + token);
        System.out.println("Link: https://10.239.39.104:5173/reset-password?token=" + token);
        System.out.println("--------------------------------------------------");

        return token;
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse googleLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // Create a new user if it doesn't exist
                    Role userRole = roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new RuntimeException("Default role not found"));
                    User newUser = new User(email.split("@")[0], "OAUTH_USER", email, userRole);
                    return userRepository.save(newUser);
                });

        return toResponse(user);
    }

    public UserResponse toResponse(User user) {
        Set<String> permissions = user.getRole().getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName(),
                permissions
        );
    }
}
