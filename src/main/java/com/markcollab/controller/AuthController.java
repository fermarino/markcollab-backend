// src/main/java/com/markcollab/controller/AuthController.java
package com.markcollab.controller;

import com.markcollab.payload.AuthRegisterRequest;
import com.markcollab.payload.AuthLoginRequest;
import com.markcollab.payload.MessageResponse;
import com.markcollab.service.AuthService;
import com.markcollab.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(
            @Valid @RequestBody AuthRegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok(new MessageResponse("Cadastro realizado com sucesso!"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(
            @Valid @RequestBody AuthLoginRequest req) {
        String token = authService.authenticate(req.getIdentifier(), req.getPassword());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "role",  jwtService.extractRole(token),
                "cpf",   jwtService.extractCpf(token)
        ));
    }
}
