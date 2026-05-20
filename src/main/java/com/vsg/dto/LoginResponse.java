package com.vsg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginResponse {
    private String token;
    private AuthUserDto user;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class AuthUserDto {
        private String id;
        private String email;
        private String name;
        private List<String> roles;
        private List<String> permissions;
        private String tenantId;
    }
}
