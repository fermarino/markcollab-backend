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

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, Object> body) {
        try {
            String role = (String) body.get("role");
            if ("EMPLOYER".equalsIgnoreCase(role)) {
                authService.registerEmployer(body);
            } else if ("FREELANCER".equalsIgnoreCase(role)) {
                authService.registerFreelancer(body);
            } else {
                return ResponseEntity.badRequest().body("Tipo de usuário inválido.");
            }
            return ResponseEntity.ok("Cadastro realizado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String identifier = body.get("identifier");
        String password = body.get("password");

        String token = authService.authenticate(identifier, password);
        String role = jwtService.extractRole(token);
        String cpf = authService.getCpf(identifier);

        return ResponseEntity.ok(Map.of("token", token, "role", role, "cpf", cpf));
    }
}
