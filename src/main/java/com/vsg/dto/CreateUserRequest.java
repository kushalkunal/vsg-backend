package com.vsg.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateUserRequest(
        @Email @NotBlank String email,
        @NotBlank String name,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
        List<String> roles
) {}
