package com.vsg.controller;

import com.vsg.dto.CreateUserRequest;
import com.vsg.dto.MessageResponse;
import com.vsg.dto.UserDto;
import com.vsg.security.JwtUtil;
import com.vsg.service.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private final UserManagementService userManagementService;
    private final JwtUtil               jwtUtil;

    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers(HttpServletRequest request) {
        return ResponseEntity.ok(userManagementService.listUsers(extractTenantId(request)));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Valid @RequestBody CreateUserRequest req,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userManagementService.createUser(extractTenantId(request), req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(
            @PathVariable UUID id,
            HttpServletRequest request) {
        return ResponseEntity.ok(userManagementService.deleteUser(extractTenantId(request), id));
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<MessageResponse> enableUser(
            @PathVariable UUID id, HttpServletRequest request) {
        return ResponseEntity.ok(userManagementService.toggleActive(extractTenantId(request), id, true));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<MessageResponse> disableUser(
            @PathVariable UUID id, HttpServletRequest request) {
        return ResponseEntity.ok(userManagementService.toggleActive(extractTenantId(request), id, false));
    }

    @PatchMapping("/{id}/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        return ResponseEntity.ok(userManagementService.adminResetPassword(
                extractTenantId(request), id, body.get("password")));
    }

    private String extractTenantId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        return jwtUtil.getTenantId(auth.substring(7));
    }
}
