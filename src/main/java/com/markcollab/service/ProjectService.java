package com.markcollab.service;

import com.markcollab.dto.EmailRequestDTO;
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
    private final EmailService emailService;

    @Autowired
    public ProjectService(ProjectRepository projectRepository,
                          EmployerRepository employerRepository,
                          FreelancerRepository freelancerRepository,
                          InterestRepository interestRepository,
                          IAService iaService,
                          EmailService emailService) {
        this.projectRepository = projectRepository;
        this.employerRepository = employerRepository;
        this.freelancerRepository = freelancerRepository;
        this.interestRepository = interestRepository;
        this.iaService = iaService;
        this.emailService = emailService;
    }

    public Project findProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
    }

    public Employer findEmployerByCpf(String employerCpf) {
        return employerRepository.findById(employerCpf)
                .orElseThrow(() -> new RuntimeException("Employer not found with cpf: " + employerCpf));
    }

    public List<ProjectDTO> getOpenProjects() {
        return projectRepository.findByOpenTrue()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ProjectDTO getProjectById(Long projectId) {
        Project project = findProjectById(projectId);
        return mapToDTO(project);
    }

    public ProjectDTO createProject(Project project, String employerCpf) {
        Employer emp = findEmployerByCpf(employerCpf);
        project.setProjectEmployer(emp);
        project.setOpen(true);
        project.setStatus("Aberto");
        Project saved = projectRepository.save(project);
        return mapToDTO(saved);
    }

    public Project updateProjectStatus(Long projectId, String newStatus, String employerCpf) {
        Project project = findProjectById(projectId);

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

    @Transactional
    public ProjectDTO hireFreelancer(Long projectId, String freelancerCpf, String employerCpf) {
        Project project = findProjectById(projectId);
        Employer employer = project.getProjectEmployer();

        if (!employer.getCpf().equals(employerCpf)) {
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

        for (Interest interest : project.getInterestedFreelancers()) {
            if (interest.getFreelancer().getCpf().equals(freelancerCpf)) {
                interest.setStatus("Aprovado");
            } else {
                interest.setStatus("Recusado");
            }
            interestRepository.save(interest);
        }

        Project saved = projectRepository.save(project);

        sendHiringConfirmationEmails(employer, freelancer, saved);

        return mapToDTO(saved);
    }

    public Interest addInterest(Long projectId, String freelancerCpf) {
        Project project = findProjectById(projectId);
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

    public ProjectDTO generateProjectDescription(Long projectId) {
        Project project = findProjectById(projectId);

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

    public Project updateProject(Long projectId, Project updated, String employerCpf) {
        Project project = findProjectById(projectId);

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

        return projectRepository.save(project);
    }

    public void deleteProject(Long projectId, String employerCpf) {
        Project project = findProjectById(projectId);

        if (!project.getProjectEmployer().getCpf().equals(employerCpf)) {
            throw new RuntimeException("Unauthorized action");
        }

        projectRepository.delete(project);
    }

    public List<ProjectDTO> getProjectsByEmployer(String employerCpf) {
        Employer emp = findEmployerByCpf(employerCpf);

        List<Project> projetos = projectRepository.findByProjectEmployer_Cpf(employerCpf);
        return projetos.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<ProjectDTO> getProjectsByFreelancer(String freelancerCpf) {
        Freelancer f = freelancerRepository.findById(freelancerCpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        List<Project> projetos = projectRepository.findByHiredFreelancer_Cpf(freelancerCpf);
        return projetos.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private ProjectDTO mapToDTO(Project project) {
        com.markcollab.dto.EmployerDTO empDto = null;
        if (project.getProjectEmployer() != null) {
            var e = project.getProjectEmployer();
            empDto = new com.markcollab.dto.EmployerDTO();
            empDto.setName(e.getName());
            empDto.setUsername(e.getUsername());
            empDto.setEmail(e.getEmail());
            empDto.setCompanyName(e.getCompanyName());
        }

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

    private void sendHiringConfirmationEmails(Employer employer, Freelancer freelancer, Project project) {
        EmailRequestDTO requestDTO = new EmailRequestDTO();

        EmailRequestDTO.ContactInfo employerInfo = new EmailRequestDTO.ContactInfo();
        employerInfo.setName(employer.getName());
        employerInfo.setEmail(employer.getEmail());

        EmailRequestDTO.ContactInfo freelancerInfo = new EmailRequestDTO.ContactInfo();
        freelancerInfo.setName(freelancer.getName());
        freelancerInfo.setEmail(freelancer.getEmail());

        // 4. Criar e popular as informações do Projeto
        EmailRequestDTO.ProjectInfo projectInfo = new EmailRequestDTO.ProjectInfo();
        projectInfo.setTitle(project.getProjectTitle());
        projectInfo.setDescription(project.getProjectDescription()); // Usando a descrição do projeto

        requestDTO.setEmployer(employerInfo);
        requestDTO.setFreelancer(freelancerInfo);
        requestDTO.setProject(projectInfo);

        emailService.enviarEmail(requestDTO);
    }
}