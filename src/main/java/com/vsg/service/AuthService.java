package com.vsg.service;

import com.vsg.dto.LoginRequest;
import com.vsg.dto.LoginResponse;
import com.vsg.dto.LoginResponse.AuthUserDto;
import com.vsg.entity.AppUser;
import com.vsg.repository.UserRepository;
import com.vsg.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest req) {
        AppUser user = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtUtil.generate(
            user.getId().toString(),
            user.getEmail(),
            user.getTenantId(),
            user.getRoles()
        );

        AuthUserDto userDto = new AuthUserDto(
            user.getId().toString(),
            user.getEmail(),
            user.getName(),
            Arrays.asList(user.getRoles()),
            user.getPermissions() != null ? Arrays.asList(user.getPermissions()) : null,
            user.getTenantId()
        );

        return new LoginResponse(token, userDto);
    }

    public AuthUserDto me(String email) {
        AppUser user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return new AuthUserDto(
            user.getId().toString(),
            user.getEmail(),
            user.getName(),
            Arrays.asList(user.getRoles()),
            user.getPermissions() != null ? Arrays.asList(user.getPermissions()) : null,
            user.getTenantId()
        );
    }
}
