package ar.meetus.tasky.controller;

import ar.meetus.tasky.dto.AuthResponse;
import ar.meetus.tasky.dto.LoginRequest;
import ar.meetus.tasky.dto.RegisterRequest;
import ar.meetus.tasky.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        // Extract token from header
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        authService.logout(jwtToken);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}