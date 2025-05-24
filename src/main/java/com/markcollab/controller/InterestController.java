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

@RestController
@RequestMapping("/api/interests")
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

    @PostMapping("/")
    public ResponseEntity<Interest> criarProposta(@RequestBody InterestRequest proposta) {

        Project project = projectRepository.findById(proposta.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));

        Freelancer freelancer = freelancerRepository.findById(proposta.getFreelancerCpf())
                .orElseThrow(() -> new ResourceNotFoundException("Freelancer não encontrado"));

        Interest interest = new Interest();
        interest.setProject(project);
        interest.setFreelancer(freelancer);
        interest.setStatus("Aguardando resposta");

        // Mapeando os campos da proposta
        interest.setProposalValue(proposta.getProposalValue());
        interest.setProposalDescription(proposta.getProposalDescription());
        interest.setDeliveryDate(proposta.getDeliveryDate());

        Interest savedInterest = interestRepository.save(interest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedInterest);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<InterestResponseDTO>> listarPropostasPorProjeto(@PathVariable Long projectId) {
        List<Interest> propostas = interestRepository.findByProject_ProjectId(projectId);

        List<InterestResponseDTO> resposta = propostas.stream().map(interest ->
                new InterestResponseDTO(
                        interest.getId(),
                        interest.getStatus(),
                        interest.getProposalValue(),
                        interest.getProposalDescription(),
                        interest.getDeliveryDate()
                )
        ).toList();

        return ResponseEntity.ok(resposta);
    }

    // Exceção personalizada para recurso não encontrado
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
