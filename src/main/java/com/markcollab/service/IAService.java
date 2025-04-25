package com.markcollab.service;

import com.markcollab.dto.ProjectIARequestDTO;
import com.markcollab.dto.ProjectIAResponseDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IAService {

    private final RestTemplate restTemplate;

    public IAService() {
        this.restTemplate = new RestTemplate();
    }

    public ProjectIAResponseDTO gerarDescricao(ProjectIARequestDTO iaRequest) {
        String url = "http://localhost:8000/gerar-descricao";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProjectIARequestDTO> requestEntity = new HttpEntity<>(iaRequest, headers);

        try {
            return restTemplate.postForObject(url, requestEntity, ProjectIAResponseDTO.class);
        } catch (Exception e) {
            // Se der erro na chamada da IA, devolve uma descrição padrão
            ProjectIAResponseDTO fallback = new ProjectIAResponseDTO();
            fallback.setDescricao("Descrição padrão gerada localmente. Microserviço de IA indisponível.");
            return fallback;
        }
    }
}
