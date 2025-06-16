package com.markcollab.service;

import com.markcollab.dto.ProjectDTO;
import com.markcollab.dto.ProjectIARequestDTO;
import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.model.Interest;
import com.markcollab.model.Project;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import com.markcollab.repository.InterestRepository;
import com.markcollab.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployerRepository employerRepository;
    private final FreelancerRepository freelancerRepository;
    private final InterestRepository interestRepository;
    private final IAService iaService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          EmployerRepository employerRepository,
                          FreelancerRepository freelancerRepository,
                          InterestRepository interestRepository,
                          IAService iaService) {
        this.projectRepository = projectRepository;
        this.employerRepository = employerRepository;
        this.freelancerRepository = freelancerRepository;
        this.interestRepository = interestRepository;
        this.iaService = iaService;
    }

    /**
     * Retorna um projeto (entidade) pelo seu ID. Lança exceção se não encontrado.
     * ESTE É O MÉTODO QUE FALTAVA.
     */
    public Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
    }

    /**
     * Retorna todos os projetos abertos (open = true).
     */
    public List<ProjectDTO> getOpenProjects() {
        return projectRepository.findByOpenTrue()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Retorna um único projeto pelo ID e mapeia para DTO.
     */
    public ProjectDTO getProjectById(Long projectId) {
        Project project = findProjectById(projectId); // Reutilizando o novo método
        return mapToDTO(project);
    }

    /**
     * Cria e salva um novo projeto, associando-o ao Employer (pelo cpf).
     */
    public ProjectDTO createProject(Project project, String employerCpf) {
        Employer emp = employerRepository.findById(employerCpf)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        project.setProjectEmployer(emp);
        project.setOpen(true);
        project.setStatus("Aberto");
        Project saved = projectRepository.save(project);
        return mapToDTO(saved);
    }

    /**
     * Atualiza somente o status de um projeto. Se “Concluído”, fecha (open = false).
     */
    public Project updateProjectStatus(Long projectId, String newStatus, String employerCpf) {
        Project project = findProjectById(projectId); // Reutilizando o novo método

        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }

        if (!List.of("Aberto", "Em andamento", "Concluído").contains(newStatus)) {
            throw new RuntimeException("Invalid status");
        }

        project.setStatus(newStatus);
        if ("Concluído".equals(newStatus)) {
            project.setOpen(false);
        }

        return projectRepository.save(project);
    }

    /**
     * Contrata um freelancer para este projeto:
     * define hiredFreelancer, fecha o projeto e atualiza o status de todas as propostas.
     * <p>
     * ANOTAÇÃO @Transactional GARANTE que o contexto de persistência fique aberto
     * enquanto iteramos em project.getInterestedFreelancers(), evitando LazyInitializationException.
     */
    @Transactional
    public ProjectDTO hireFreelancer(Long projectId, String freelancerCpf, String employerCpf) {
        Project project = findProjectById(projectId); // Reutilizando o novo método

        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }
        if (!project.isOpen()) {
            throw new RuntimeException("Project is already closed");
        }

        Freelancer freelancer = freelancerRepository.findById(freelancerCpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        // Define o freelancer contratado, fecha o projeto e altera o status
        project.setHiredFreelancer(freelancer);
        project.setOpen(false);
        project.setStatus("Em andamento");

        // Atualiza o status de cada Interest (proposta) deste projeto
        // (Como estamos dentro de @Transactional, a lista vem carregada e podemos iterar)
        for (Interest interest : project.getInterestedFreelancers()) {
            if (interest.getFreelancer().getCpf().equals(freelancerCpf)) {
                interest.setStatus("Aprovado");
            } else {
                interest.setStatus("Recusado");
            }
            interestRepository.save(interest);
        }

        Project saved = projectRepository.save(project);
        return mapToDTO(saved);
    }

    /**
     * Adiciona uma proposta (Interest) de freelancer a este projeto.
     */
    public Interest addInterest(Long projectId, String freelancerCpf) {
        Project project = findProjectById(projectId); // Reutilizando o novo método
        if (!project.isOpen()) {
            throw new RuntimeException("Project is not open");
        }

        Freelancer freelancer = freelancerRepository.findById(freelancerCpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        Interest interest = new Interest();
        interest.setProject(project);
        interest.setFreelancer(freelancer);
        interest.setStatus("Aguardando resposta");
        return interestRepository.save(interest);
    }

    /**
     * Gera descrição automática via IA e salva no projeto.
     */
    public ProjectDTO generateProjectDescription(Long projectId) {
        Project project = findProjectById(projectId); // Reutilizando o novo método

        var iaRequest = ProjectIARequestDTO.builder()
                .projectTitle(project.getProjectTitle())
                .projectSpecifications(project.getProjectSpecifications())
                .projectDeadline("15 dias")
                .build();

        var iaResponse = iaService.gerarDescricao(iaRequest);
        project.setProjectDescription(iaResponse.getDescricao());

        Project updated = projectRepository.save(project);
        return mapToDTO(updated);
    }

    /**
     * Atualiza dados gerais de um projeto (título, descrição, specs, preço, prazo, status).
     */
    public Project updateProject(Long projectId, Project updated, String employerCpf) {
        Project project = findProjectById(projectId); // Reutilizando o novo método

        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }

        project.setProjectTitle(updated.getProjectTitle());
        project.setProjectDescription(updated.getProjectDescription());
        project.setProjectSpecifications(updated.getProjectSpecifications());
        project.setProjectPrice(updated.getProjectPrice());
        project.setDeadline(updated.getDeadline());
        project.setStatus(updated.getStatus());

        if ("Concluído".equals(updated.getStatus())) {
            project.setOpen(false);
        }

        Project saved = projectRepository.save(project);
        return saved;
    }

    /**
     * Deleta um projeto, somente se o employerCpf bater com o do projeto.
     */
    public void deleteProject(Long projectId, String employerCpf) {
        Project project = findProjectById(projectId); // Reutilizando o novo método

        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }

        projectRepository.delete(project);
    }

    /**
     * Retorna TODOS os projetos (abertos ou não) de um empregador.
     */
    public List<ProjectDTO> getProjectsByEmployer(String employerCpf) {
        Employer emp = employerRepository.findById(employerCpf)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        List<Project> projetos = projectRepository.findByProjectEmployer_Cpf(employerCpf);
        return projetos.stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Retorna TODOS os projetos em que um determinado freelancer foi contratado.
     */
    public List<ProjectDTO> getProjectsByFreelancer(String freelancerCpf) {
        Freelancer f = freelancerRepository.findById(freelancerCpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        List<Project> projetos = projectRepository.findByHiredFreelancer_Cpf(freelancerCpf);
        return projetos.stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Mapeia uma entidade Project inteira para o DTO que expomos via JSON.
     */
    private ProjectDTO mapToDTO(Project project) {
        // Mapeia dados do empregador
        com.markcollab.dto.EmployerDTO empDto = null;
        if (project.getProjectEmployer() != null) {
            var e = project.getProjectEmployer();
            empDto = new com.markcollab.dto.EmployerDTO();
            empDto.setName(e.getName());
            empDto.setUsername(e.getUsername());
            empDto.setEmail(e.getEmail());
            empDto.setCompanyName(e.getCompanyName());
        }

        // Mapeia dados do freelancer contratado (se houver)
        com.markcollab.dto.FreelancerDTO frDto = null;
        if (project.getHiredFreelancer() != null) {
            var f = project.getHiredFreelancer();
            frDto = new com.markcollab.dto.FreelancerDTO();
            frDto.setName(f.getName());
            frDto.setUsername(f.getUsername());
            frDto.setEmail(f.getEmail());
            frDto.setPortfolioLink(f.getPortfolioLink());
            frDto.setAboutMe(f.getAboutMe());
            frDto.setExperience(f.getExperience());
        }

        return ProjectDTO.builder()
                .projectId(project.getProjectId())
                .projectTitle(project.getProjectTitle())
                .projectDescription(project.getProjectDescription())
                .projectSpecifications(project.getProjectSpecifications())
                .projectPrice(project.getProjectPrice())
                .open(project.isOpen())
                .status(project.getStatus())
                .deadline(project.getDeadline())
                .projectEmployer(empDto)
                .hiredFreelancer(frDto)
                .build();
    }
}
