package com.markcollab.controller;

import com.markcollab.dto.ProjectIARequestDTO;
import com.markcollab.dto.ProjectIAResponseDTO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/ia")
public class IAController {
    
    @PostMapping("/gerar-descricao")
    public ResponseEntity<ProjectIAResponseDTO> gerarDescricao(@RequestBody ProjectIARequestDTO projeto) {
        String url = "http://localhost:8000/gerar-descricao";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProjectIARequestDTO> request = new HttpEntity<>(projeto, headers);

        try {
            ResponseEntity<ProjectIAResponseDTO> response =
                restTemplate.postForEntity(url, request, ProjectIAResponseDTO.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            ProjectIAResponseDTO erro = new ProjectIAResponseDTO();
            erro.setDescricao("Erro ao chamar IA: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
        }
    }
}
