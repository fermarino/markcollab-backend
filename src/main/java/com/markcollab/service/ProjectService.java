package com.markcollab.service;

import com.markcollab.dto.EmployerDTO;
import com.markcollab.dto.FreelancerDTO;
import com.markcollab.dto.ProjectDTO;
import com.markcollab.dto.ProjectIARequestDTO;
import com.markcollab.dto.ProjectIAResponseDTO;
import com.markcollab.model.*;
import com.markcollab.repository.*;
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

    public List<ProjectDTO> getOpenProjects() {
        return projectRepository.findByOpenTrue().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ProjectDTO getProjectById(Long projectId) {
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));
            return mapToDTO(project);
        } catch (Exception e) {
            e.printStackTrace(); // ou log.error
            throw new RuntimeException("Erro ao buscar projeto: " + e.getMessage());
        }
    }

    public List<ProjectDTO> getByEmployer(String cpf) {
        return projectRepository.findByProjectEmployerCpf(cpf).stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ProjectDTO createProject(Project project, String employerCpf) {
        Employer emp = employerRepository.findById(employerCpf)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        project.setProjectEmployer(emp);
        project.setOpen(true);
        project.setStatus("Aberto");
        return mapToDTO(projectRepository.save(project));
    }

    public Project updateProject(Long projectId, Project updated, String employerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getProjectEmployer().getCpf().equals(employerCpf))
            throw new RuntimeException("Unauthorized action");

        project.setProjectTitle(updated.getProjectTitle());
        project.setProjectDescription(updated.getProjectDescription());
        project.setProjectSpecifications(updated.getProjectSpecifications());
        project.setProjectPrice(updated.getProjectPrice());
        project.setDeadline(updated.getDeadline());
        project.setStatus(updated.getStatus());

        return projectRepository.save(project);
    }

    public void deleteProject(Long projectId, String employerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getProjectEmployer().getCpf().equals(employerCpf))
            throw new RuntimeException("Unauthorized action");

        projectRepository.delete(project);
    }

    public Project updateProjectStatus(Long projectId, String newStatus, String employerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getProjectEmployer().getCpf().equals(employerCpf))
            throw new RuntimeException("Unauthorized action");

        if (!List.of("Aberto", "Em andamento", "Concluído").contains(newStatus))
            throw new RuntimeException("Invalid status");

        project.setStatus(newStatus);
        if ("Concluído".equals(newStatus)) {
            project.setOpen(false);
        }

        return projectRepository.save(project);
    }

    public ProjectDTO hireFreelancer(Long projectId, String freelancerCpf, String employerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getProjectEmployer().getCpf().equals(employerCpf))
            throw new RuntimeException("Unauthorized action");

        if (!project.isOpen())
            throw new RuntimeException("Project is already closed");

        Freelancer freelancer = freelancerRepository.findById(freelancerCpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        project.setHiredFreelancer(freelancer);
        project.setOpen(false);
        project.setStatus("Em andamento");

        project.getInterestedFreelancers().forEach(interest -> {
            if (interest.getFreelancer().getCpf().equals(freelancerCpf))
                interest.setStatus("Aprovado");
            else
                interest.setStatus("Recusado");
            interestRepository.save(interest);
        });

        return mapToDTO(projectRepository.save(project));
    }

    public Interest addInterest(Long projectId, String freelancerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.isOpen())
            throw new RuntimeException("Project is not open");

        Freelancer freelancer = freelancerRepository.findById(freelancerCpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        Interest interest = new Interest();
        interest.setProject(project);
        interest.setFreelancer(freelancer);
        interest.setStatus("Aguardando resposta");

        return interestRepository.save(interest);
    }

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

        return mapToDTO(projectRepository.save(project));
    }

    private ProjectDTO mapToDTO(Project project) {
        EmployerDTO empDto = null;
        if (project.getProjectEmployer() != null) {
            Employer e = project.getProjectEmployer();
            empDto = new EmployerDTO();
            empDto.setName(e.getName());
            empDto.setUsername(e.getUsername());
            empDto.setEmail(e.getEmail());
            empDto.setCompanyName(e.getCompanyName());
        }

        FreelancerDTO frDto = null;
        if (project.getHiredFreelancer() != null) {
            Freelancer f = project.getHiredFreelancer();
            frDto = new FreelancerDTO();
            frDto.setName(f.getName());
            frDto.setUsername(f.getUsername());
            frDto.setEmail(f.getEmail());
            frDto.setPortfolioLink(f.getPortfolioLink());
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