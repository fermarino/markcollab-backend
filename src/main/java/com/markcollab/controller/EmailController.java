package com.markcollab.controller;

import com.markcollab.dto.EmailRequestDTO;
import com.markcollab.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/enviar-contato")
    public ResponseEntity<String> enviarContatos(@RequestBody EmailRequestDTO dto) {
        System.out.println("📨 DTO recebido:");
        System.out.println(dto);
        String resposta = emailService.enviarEmail(dto);
        return ResponseEntity.ok(resposta);
    }
}
