// src/main/java/com/markcollab/controller/InterestController.java
package com.markcollab.controller;

import com.markcollab.dto.InterestRequest;
import com.markcollab.dto.InterestResponseDTO;
import com.markcollab.model.Freelancer;
import com.markcollab.model.Interest;
import com.markcollab.model.Project;
import com.markcollab.repository.FreelancerRepository;
import com.markcollab.repository.InterestRepository;
import com.markcollab.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/interests")
@CrossOrigin(origins = "https://markcollab-backend.onrender.com")
public class InterestController {

    private final InterestRepository interestRepository;
    private final ProjectRepository projectRepository;
    private final FreelancerRepository freelancerRepository;

    public InterestController(InterestRepository interestRepository,
                              ProjectRepository projectRepository,
                              FreelancerRepository freelancerRepository) {
        this.interestRepository = interestRepository;
        this.projectRepository = projectRepository;
        this.freelancerRepository = freelancerRepository;
    }

    /**
     * Cria uma nova proposta (Interest) para este projeto.
     * POST /api/interests/
     */
    @PostMapping("/")
    public ResponseEntity<Interest> criarProposta(@Valid @RequestBody InterestRequest proposta) {
        Project project = projectRepository.findById(proposta.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));

        Freelancer freelancer = freelancerRepository.findById(proposta.getFreelancerCpf())
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado"));

        Interest interest = new Interest();
        interest.setProject(project);
        interest.setFreelancer(freelancer);
        interest.setStatus("Aguardando resposta");
        interest.setProposalValue(proposta.getProposalValue());
        interest.setProposalDescription(proposta.getProposalDescription());
        interest.setDeliveryDate(proposta.getDeliveryDate());

        Interest savedInterest = interestRepository.save(interest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedInterest);
    }

    /**
     * Lista todas as propostas para um determinado projeto.
     * GET /api/interests/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<InterestResponseDTO>> listarPropostasPorProjeto(@PathVariable Long projectId) {
        List<Interest> propostas = interestRepository.findByProject_ProjectId(projectId);

        List<InterestResponseDTO> resposta = propostas.stream().map(interest -> {
            Freelancer freelancer = interest.getFreelancer();
            return new InterestResponseDTO(
                    interest.getId(),
                    interest.getStatus(),
                    interest.getProposalValue(),
                    interest.getProposalDescription(),
                    interest.getDeliveryDate(),
                    freelancer.getCpf(),
                    freelancer.getName()
            );
        }).toList();

        return ResponseEntity.ok(resposta);
    }

    /**
     * Atualiza apenas o status de uma proposta (Interest).
     * PUT /api/interests/{interestId}/status
     * Body: "Aprovado" ou "Recusado"
     */
    @PutMapping("/{interestId}/status")
    public ResponseEntity<InterestResponseDTO> updateInterestStatus(
            @PathVariable Long interestId,
            @RequestBody String newStatus) {

        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new ResourceNotFoundException("Interest não encontrado"));

        interest.setStatus(newStatus);
        interestRepository.save(interest);

        Freelancer freelancer = interest.getFreelancer();
        InterestResponseDTO dto = new InterestResponseDTO(
                interest.getId(),
                interest.getStatus(),
                interest.getProposalValue(),
                interest.getProposalDescription(),
                interest.getDeliveryDate(),
                freelancer.getCpf(),
                freelancer.getName()
        );
        return ResponseEntity.ok(dto);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
