package com.markcollab.controller;

import com.markcollab.service.AuthService;
import com.markcollab.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

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

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private static final String NAME_REGEX = "^[a-zA-Z\\s]+$";  // Regex para permitir apenas letras e espaços

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid Map<String, Object> body) {
        String cpf = (String) body.get("cpf");
        String email = (String) body.get("email");
        String name = (String) body.get("name");  // Nome completo
        String password = (String) body.get("password");
        String role = (String) body.get("role");

        // Validação de campos obrigatórios (não podem ser nulos ou vazios)
        if (cpf == null || cpf.isEmpty()) {
            return ResponseEntity.badRequest().body("Preenchimento obrigatório");
        }
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Preenchimento obrigatório");
        }
        if (name == null || name.isEmpty()) {
            return ResponseEntity.badRequest().body("Preenchimento obrigatório");
        } else if (!Pattern.matches(NAME_REGEX, name)) {  // Verificação de caracteres inválidos
            return ResponseEntity.badRequest().body("O nome não pode conter caracteres inválidos como <, >, {, }.' ");
        }
        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body("Preenchimento obrigatório");
        }
        if (role == null || role.isEmpty()) {
            return ResponseEntity.badRequest().body("Preenchimento obrigatório");
        }

        // Validação do CPF (verifica se tem 11 dígitos numéricos)
        if (!isValidCpf(cpf)) {
            return ResponseEntity.badRequest().body("CPF inválido");
        }

        // Validação do E-mail (verifica o formato com a regex mais robusta)
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            return ResponseEntity.badRequest().body("E-mail inválido");
        }

        // Validação da Senha (tamanho entre 6 e 40 caracteres)
        if (password.length() < 6) {
            return ResponseEntity.badRequest().body("Senha deve conter pelo menos 6 caracteres");
        }
        if (password.length() > 40) {
            return ResponseEntity.badRequest().body("Senha deve conter no máximo 40 caracteres");
        }

        // Verificação do papel e registro
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

    // Método simples de validação de CPF (verifica se tem 11 dígitos numéricos)
    private boolean isValidCpf(String cpf) {
        return cpf != null && cpf.matches("\\d{11}");
    }
}
