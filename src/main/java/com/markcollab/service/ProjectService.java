package com.markcollab.service;

import com.markcollab.dto.EmployerDTO;
import com.markcollab.dto.FreelancerDTO;
import com.markcollab.dto.ProjectDTO;
import com.markcollab.model.*;
import com.markcollab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private EmployerRepository employerRepository;

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
        // Mapear Employer para EmployerDTO
        Employer employer = project.getProjectEmployer();
        EmployerDTO employerDTO = new EmployerDTO();
        employerDTO.setName(employer.getName());
        employerDTO.setUsername(employer.getUsername());
        employerDTO.setEmail(employer.getEmail());
        employerDTO.setCompanyName(employer.getCompanyName());

        // Mapear Freelancer para FreelancerDTO
        FreelancerDTO freelancerDTO = null;
        if (project.getHiredFreelancer() != null) {
            Freelancer freelancer = project.getHiredFreelancer();
            freelancerDTO = new FreelancerDTO();
            freelancerDTO.setName(freelancer.getName());
            freelancerDTO.setUsername(freelancer.getUsername());
            freelancerDTO.setEmail(freelancer.getEmail());
            freelancerDTO.setPortfolioLink(freelancer.getPortfolioLink());
        }

        // Mapear Project para ProjectDTO
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectTitle(project.getProjectTitle());
        projectDTO.setProjectDescription(project.getProjectDescription());
        projectDTO.setProjectSpecifications(project.getProjectSpecifications());
        projectDTO.setProjectPrice(project.getProjectPrice());
        projectDTO.setOpen(project.isOpen());
        projectDTO.setStatus(project.getStatus());
        projectDTO.setProjectEmployer(employerDTO);
        projectDTO.setHiredFreelancer(freelancerDTO);

        return projectDTO;
    }

    public ProjectDTO hireFreelancer(Long projectId, String freelancerCpf, String employerCpf) {
        // Buscar o projeto
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Verificar se o contratante é o dono do projeto
        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }

        // Verificar se o projeto está aberto
        if (!project.isOpen()) {
            throw new RuntimeException("Project is already closed");
        }

        // Buscar o freelancer
        Freelancer freelancer = freelancerRepository.findById(freelancerCpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        // Atualizar o projeto com o freelancer contratado
        project.setHiredFreelancer(freelancer);
        project.setOpen(false); // Marcar o projeto como fechado para novos interesses
        project.setStatus("Em andamento"); // Alterar o status para "Em andamento"

        // Atualizar os interesses no projeto
        project.getInterestedFreelancers().forEach(interest -> {
            if (interest.getFreelancer().getCpf().equals(freelancerCpf)) {
                interest.setStatus("Aprovado");
            } else {
                interest.setStatus("Recusado");
            }
            interestRepository.save(interest);
        });

        // Salvar o projeto e retornar o DTO
        Project savedProject = projectRepository.save(project);
        return mapToDTO(savedProject);
    }


    // Atualizar status do projeto
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

    // Atualizar projeto
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

    // Deletar projeto
    public void deleteProject(Long projectId, String employerCpf) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }

        projectRepository.delete(project);
    }

    // Listar projetos de um empregador
    public List<Project> getProjectsByEmployer(String employerCpf) {
        return projectRepository.findByProjectEmployerCpf(employerCpf);
    }

    // Listar projetos abertos
    public List<Project> getOpenProjects() {
        return projectRepository.findByOpenTrue();
    }

    // Adicionar interesse de freelancer
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
