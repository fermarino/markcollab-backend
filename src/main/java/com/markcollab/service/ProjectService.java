package com.markcollab.service;

import com.markcollab.dto.EmployerDTO;
import com.markcollab.dto.FreelancerDTO;
import com.markcollab.dto.ProjectDTO;
import com.markcollab.dto.ProjectIARequestDTO;
import com.markcollab.dto.ProjectIAResponseDTO;
import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.model.Interest;
import com.markcollab.model.Project;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import com.markcollab.repository.InterestRepository;
import com.markcollab.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private IAService iaService;  // ✅ Injetando IAService aqui

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    // Criar projeto
    public ProjectDTO createProject(Project project, String employerCpf) {
        Employer employer = employerRepository.findById(employerCpf)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        project.setProjectEmployer(employer);
        project.setOpen(true);
        project.setStatus("Aberto");
        Project savedProject = projectRepository.save(project);

        return mapToDTO(savedProject);
    }

    // Mapear projeto para DTO
    private ProjectDTO mapToDTO(Project project) {
        Employer employer = project.getProjectEmployer();
        EmployerDTO employerDTO = new EmployerDTO();
        employerDTO.setName(employer.getName());
        employerDTO.setUsername(employer.getUsername());
        employerDTO.setEmail(employer.getEmail());
        employerDTO.setCompanyName(employer.getCompanyName());

        FreelancerDTO freelancerDTO = null;
        if (project.getHiredFreelancer() != null) {
            Freelancer freelancer = project.getHiredFreelancer();
            freelancerDTO = new FreelancerDTO();
            freelancerDTO.setName(freelancer.getName());
            freelancerDTO.setUsername(freelancer.getUsername());
            freelancerDTO.setEmail(freelancer.getEmail());
            freelancerDTO.setPortfolioLink(freelancer.getPortfolioLink());
        }

        return ProjectDTO.builder()
                .projectId(project.getProjectId())
                .projectTitle(project.getProjectTitle())
                .projectDescription(project.getProjectDescription())
                .projectSpecifications(project.getProjectSpecifications())
                .projectPrice(project.getProjectPrice())
                .open(project.isOpen())
                .status(project.getStatus())
                .projectEmployer(employerDTO)
                .hiredFreelancer(freelancerDTO)
                .build();
    }

    // ✅ Novo método: gerar descrição com IA
    public ProjectDTO generateProjectDescription(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectIARequestDTO iaRequest = ProjectIARequestDTO.builder()
                .projectTitle(project.getProjectTitle())
                .projectSpecifications(project.getProjectSpecifications())
                .projectDeadline("15 dias")
                .build();

        ProjectIAResponseDTO iaResponse = iaService.gerarDescricao(iaRequest);

        project.setProjectDescription(iaResponse.getDescricao());

        Project savedProject = projectRepository.save(project);
        return mapToDTO(savedProject);
    }

    public ProjectDTO hireFreelancer(Long projectId, String freelancerCpf, String employerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }

        if (!project.isOpen()) {
            throw new RuntimeException("Project is already closed");
        }

        Freelancer freelancer = freelancerRepository.findById(freelancerCpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        project.setHiredFreelancer(freelancer);
        project.setOpen(false);
        project.setStatus("Em andamento");

        project.getInterestedFreelancers().forEach(interest -> {
            if (interest.getFreelancer().getCpf().equals(freelancerCpf)) {
                interest.setStatus("Aprovado");
            } else {
                interest.setStatus("Recusado");
            }
            interestRepository.save(interest);
        });

        Project savedProject = projectRepository.save(project);
        return mapToDTO(savedProject);
    }

    public Project updateProjectStatus(Long projectId, String newStatus, String employerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }

        if (!List.of("Aberto", "Em andamento", "Concluído").contains(newStatus)) {
            throw new RuntimeException("Invalid status: Status must be 'Aberto', 'Em andamento', or 'Concluído'");
        }

        project.setStatus(newStatus);

        if ("Concluído".equals(newStatus)) {
            project.setOpen(false);
        }

        return projectRepository.save(project);
    }

    // ✅ Método para atualizar projeto (resolve o erro no controller)
    public Project updateProject(Long projectId, Project updatedProject, String employerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }

        project.setProjectTitle(updatedProject.getProjectTitle());
        project.setProjectDescription(updatedProject.getProjectDescription());
        project.setProjectSpecifications(updatedProject.getProjectSpecifications());
        project.setProjectPrice(updatedProject.getProjectPrice());

        return projectRepository.save(project);
    }

    public void deleteProject(Long projectId, String employerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }

        projectRepository.delete(project);
    }

    public List<Project> getProjectsByEmployer(String employerCpf) {
        return projectRepository.findByProjectEmployerCpf(employerCpf);
    }

    public List<Project> getOpenProjects() {
        return projectRepository.findByOpenTrue();
    }

    public Interest addInterest(Long projectId, String freelancerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.isOpen()) {
            throw new RuntimeException("Project is not open for new interests");
        }

        Freelancer freelancer = freelancerRepository.findById(freelancerCpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        Interest interest = new Interest();
        interest.setProject(project);
        interest.setFreelancer(freelancer);
        interest.setStatus("Aguardando resposta");

        return interestRepository.save(interest);
    }
}
