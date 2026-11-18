package co.edu.unbosque.parcialfinal.dto;

public record AuthResponseDTO(String token, String role, String username, long expiresInMinutes) {}
