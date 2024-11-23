package com.markcollab.service;

import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private EmployerRepository employerRepository;


    // Método de teste para salvar manualmente um Freelancer
    @PostConstruct
    public void testSaveFreelancer() {
        try {
            Freelancer freelancer = new Freelancer();
            freelancer.setName("Jane Doe");
            freelancer.setUsername("jane_doe");
            freelancer.setEmail("jane.doe@example.com");
            freelancer.setCpf("98765432100");
            freelancer.setPortfolioLink("http://portfolio.example.com");
            freelancer.setRole("FREELANCER");

            freelancerRepository.save(freelancer);
            System.out.println("Freelancer saved successfully!");
        } catch (Exception e) {
            System.err.println("Error saving Freelancer: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Criar Freelancer
    public Freelancer registerFreelancer(Freelancer freelancer) {
        freelancer.setRole("FREELANCER"); // Define o papel como Freelancer
        return freelancerRepository.save(freelancer);
    }

    // Criar Employer
    public Employer registerEmployer(Employer employer) {
        // Validações de unicidade
        if (employerRepository.existsByUsername(employer.getUsername()) ||
                employerRepository.existsByEmail(employer.getEmail()) ||
                employerRepository.existsByCpf(employer.getCpf())) {
            throw new RuntimeException("Duplicate entry for username, email, or CPF");
        }

        // Define o papel como Employer
        employer.setRole("EMPLOYER");
        return employerRepository.save(employer);
    }


    // Obter todos os Freelancers
    public List<Freelancer> getAllFreelancers() {
        return freelancerRepository.findAll();
    }

    // Obter todos os Employers
    public List<Employer> getAllEmployers() {
        return employerRepository.findAll();
    }

    // Buscar Freelancers por Nome
    public List<Freelancer> findFreelancersByName(String name) {
        return freelancerRepository.findByNameContainingIgnoreCase(name);
    }

    // Buscar Freelancers por Username
    public List<Freelancer> findFreelancersByUsername(String username) {
        return freelancerRepository.findByUsernameContainingIgnoreCase(username);
    }

    // Buscar Employers por Nome
    public List<Employer> findEmployersByName(String name) {
        return employerRepository.findByNameContainingIgnoreCase(name);
    }

    // Buscar Employers por Username
    public List<Employer> findEmployersByUsername(String username) {
        return employerRepository.findByUsernameContainingIgnoreCase(username);
    }

    // Atualizar Freelancer
    public Freelancer updateFreelancer(Long id, Freelancer updatedFreelancer) {
        Optional<Freelancer> optionalFreelancer = freelancerRepository.findById(id);

        if (optionalFreelancer.isEmpty()) {
            throw new RuntimeException("Freelancer not found with ID: " + id);
        }

        Freelancer freelancer = optionalFreelancer.get();
        freelancer.setName(updatedFreelancer.getName());
        freelancer.setUsername(updatedFreelancer.getUsername());
        freelancer.setEmail(updatedFreelancer.getEmail());
        freelancer.setPortfolioLink(updatedFreelancer.getPortfolioLink());

        return freelancerRepository.save(freelancer);
    }

    // Atualizar Employer
    public Employer updateEmployer(Long id, Employer updatedEmployer) {
        Optional<Employer> optionalEmployer = employerRepository.findById(id);

        if (optionalEmployer.isEmpty()) {
            throw new RuntimeException("Employer not found with ID: " + id);
        }

        Employer employer = optionalEmployer.get();
        employer.setName(updatedEmployer.getName());
        employer.setUsername(updatedEmployer.getUsername());
        employer.setEmail(updatedEmployer.getEmail());
        employer.setCompanyName(updatedEmployer.getCompanyName());

        return employerRepository.save(employer);
    }

    // Deletar Freelancer
    public void deleteFreelancer(Long id) {
        Optional<Freelancer> optionalFreelancer = freelancerRepository.findById(id);

        if (optionalFreelancer.isEmpty()) {
            throw new RuntimeException("Freelancer not found with ID: " + id);
        }

        freelancerRepository.delete(optionalFreelancer.get());
    }

    // Deletar Employer
    public void deleteEmployer(Long id) {
        Optional<Employer> optionalEmployer = employerRepository.findById(id);

        if (optionalEmployer.isEmpty()) {
            throw new RuntimeException("Employer not found with ID: " + id);
        }

        employerRepository.delete(optionalEmployer.get());
    }
}
