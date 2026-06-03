package ro.ubbcluj.assignment.dto;

import java.util.Set;

public record UserResponse(Long id, String username, String email, String role, Set<String> permissions) {}
