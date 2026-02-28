package com.bpd.prayertime.identity.rest;

import com.bpd.prayertime.config.JwtUtils;
import com.bpd.prayertime.identity.domain.User;
import com.bpd.prayertime.identity.domain.UserRepository;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            String token = jwtUtils.generateToken(userOpt.get().getEmail());
            return ResponseEntity.ok(new LoginResponse("Login successful", userOpt.get().getEmail(), token));
        }

        return ResponseEntity.status(401).body("Invalid email or password");
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private final String message;
        private final String email;
        private final String token;
    }
}
