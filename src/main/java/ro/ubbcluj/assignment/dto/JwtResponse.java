package ro.ubbcluj.assignment.dto;

import java.util.Set;

public record JwtResponse(
    String token,
    Long id,
    String username,
    String email,
    String role,
    Set<String> permissions
) {}
