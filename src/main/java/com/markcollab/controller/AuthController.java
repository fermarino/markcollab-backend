package com.markcollab.controller;

import com.markcollab.service.AuthService;
import com.markcollab.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, Object> body) {
        try {
            String role = (String) body.get("role");
            if ("EMPLOYER".equalsIgnoreCase(role)) {
                authService.registerEmployer(body);
                return ResponseEntity.ok("Empregador registrado com sucesso!");
            } else if ("FREELANCER".equalsIgnoreCase(role)) {
                authService.registerFreelancer(body);
                return ResponseEntity.ok("Freelancer registrado com sucesso!");
            } else {
                return ResponseEntity.badRequest().body("Role inválido.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String token = authService.authenticate(body.get("username"), body.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }
}
