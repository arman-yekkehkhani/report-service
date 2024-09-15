package edu.personal.report.controller.auth;

public record LoginResponse(String token, long expiresIn) {
}
