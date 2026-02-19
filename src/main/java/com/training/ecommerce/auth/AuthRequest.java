package com.training.ecommerce.auth;

public record AuthRequest(String firstName, String lastName, String email, String password) {}
