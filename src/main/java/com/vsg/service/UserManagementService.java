package com.vsg.service;

import com.vsg.dto.CreateUserRequest;
import com.vsg.dto.MessageResponse;
import com.vsg.dto.UserDto;
import com.vsg.entity.AppUser;
import com.vsg.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private static final int MAX_USERS_PER_TENANT = 5;

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> listUsers(String tenantId) {
        return userRepository.findByTenantIdOrderByCreatedAtAsc(tenantId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public UserDto createUser(String tenantId, CreateUserRequest req) {
        long count = userRepository.countByTenantId(tenantId);
        if (count >= MAX_USERS_PER_TENANT) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Maximum of " + MAX_USERS_PER_TENANT + " users allowed per tenant.");
        }
        if (userRepository.existsByEmailAndTenantId(req.email().toLowerCase(), tenantId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A user with that email already exists.");
        }

        AppUser user = new AppUser();
        user.setTenantId(tenantId);
        user.setEmail(req.email().toLowerCase());
        user.setName(req.name());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setActive(true);

        String[] roles = (req.roles() != null && !req.roles().isEmpty())
                ? req.roles().toArray(new String[0])
                : new String[]{"COUNSELLOR"};
        user.setRoles(roles);

        return toDto(userRepository.save(user));
    }

    @Transactional
    public MessageResponse deleteUser(String tenantId, UUID userId) {
        AppUser user = userRepository.findById(userId)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
        return new MessageResponse("User deleted.");
    }

    @Transactional
    public MessageResponse toggleActive(String tenantId, UUID userId, boolean active) {
        AppUser user = userRepository.findById(userId)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!active && Arrays.asList(user.getRoles()).contains("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin accounts cannot be disabled.");
        }
        user.setActive(active);
        userRepository.save(user);
        return new MessageResponse("User " + (active ? "enabled" : "disabled") + ".");
    }

    @Transactional
    public MessageResponse adminResetPassword(String tenantId, UUID userId, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters.");
        }
        AppUser user = userRepository.findById(userId)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return new MessageResponse("Password reset successfully.");
    }

    private UserDto toDto(AppUser u) {
        return new UserDto(
                u.getId().toString(),
                u.getEmail(),
                u.getName(),
                Arrays.asList(u.getRoles()),
                u.getTenantId(),
                u.isActive(),
                u.getCreatedAt());
    }
}
