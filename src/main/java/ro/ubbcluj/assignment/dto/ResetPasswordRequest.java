package ro.ubbcluj.assignment.dto;

public record ResetPasswordRequest(String token, String newPassword) {}
