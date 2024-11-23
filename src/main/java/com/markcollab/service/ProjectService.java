package com.markcollab.service;

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
    public Project createProject(Project project, String employerCpf) {
        Employer employer = employerRepository.findById(employerCpf)
                .orElseThrow(() -> new RuntimeException("Employer not found"));
        project.setProjectEmployer(employer);
        project.setOpen(true); // Projeto começa aberto para interesse
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

    // Contratar freelancer
    public Project hireFreelancer(Long projectId, String freelancerCpf, String employerCpf) {
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

        // Atualiza o projeto
        project.setHiredFreelancer(freelancer);
        project.setOpen(false);

        // Atualiza os interesses
        project.getInterestedFreelancers().forEach(interest -> {
            if (interest.getFreelancer().getCpf().equals(freelancerCpf)) {
                interest.setStatus("Aprovado");
            } else {
                interest.setStatus("Recusado");
            }
            interestRepository.save(interest);
        });

        return projectRepository.save(project);
    }
}
