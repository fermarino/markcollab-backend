package com.markcollab.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.markcollab.dto.EmailRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    @Value("${email.api.url}")
    private String emailApiUrl;

    private final RestTemplate restTemplate;

    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String enviarEmail(EmailRequestDTO dto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<EmailRequestDTO> request = new HttpEntity<>(dto, headers);

            System.out.println("➡ Enviando requisição para: " + emailApiUrl);
            System.out.println("📨 Payload enviado: " + new ObjectMapper().writeValueAsString(dto));

            ResponseEntity<String> response = restTemplate.postForEntity(emailApiUrl, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao enviar requisição para o serviço de e-mail: " + e.getMessage();
        }
    }
}
