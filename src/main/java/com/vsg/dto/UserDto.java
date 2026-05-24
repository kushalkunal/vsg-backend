package com.vsg.dto;

import java.time.LocalDateTime;
import java.util.List;

public record UserDto(
        String id,
        String email,
        String name,
        List<String> roles,
        String tenantId,
        boolean isActive,
        LocalDateTime createdAt
) {}
