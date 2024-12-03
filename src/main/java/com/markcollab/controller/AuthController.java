package com.markcollab.controller;

import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.service.AuthService;
import com.markcollab.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid Map<String, Object> body) {
        String role = (String) body.get("role");

        if ("EMPLOYER".equalsIgnoreCase(role)) {
            authService.registerEmployer(body);
            return ResponseEntity.ok("Employer registered successfully!");
        } else if ("FREELANCER".equalsIgnoreCase(role)) {
            authService.registerFreelancer(body);
            return ResponseEntity.ok("Freelancer registered successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid role. Use 'EMPLOYER' or 'FREELANCER'.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String token = authService.authenticate(body.get("username"), body.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }
}
