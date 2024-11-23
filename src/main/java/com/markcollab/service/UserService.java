package com.markcollab.service;

import com.markcollab.model.AbstractUser;
import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Criar Freelancer
    public Freelancer registerFreelancer(Freelancer freelancer) {
        validateUser(freelancer.getCpf(), freelancer.getEmail(), freelancer.getUsername());
        freelancer.setRole("FREELANCER");
        freelancer.setPassword(passwordEncoder.encode(freelancer.getPassword()));
        return freelancerRepository.save(freelancer);
    }

    // Criar Employer
    public Employer registerEmployer(Employer employer) {
        validateUser(employer.getCpf(), employer.getEmail(), employer.getUsername());
        employer.setRole("EMPLOYER");
        employer.setPassword(passwordEncoder.encode(employer.getPassword()));
        return employerRepository.save(employer);
    }

    // Validações de duplicidade
    private void validateUser(String cpf, String email, String username) {
        if (freelancerRepository.existsByUsername(username) || employerRepository.existsByUsername(username)) {
            throw new RuntimeException("Username is already in use.");
        }
        if (freelancerRepository.existsByEmail(email) || employerRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already in use.");
        }
        if (freelancerRepository.existsById(cpf) || employerRepository.existsById(cpf)) {
            throw new RuntimeException("CPF is already in use.");
        }
    }

    // Obter todos os Freelancers
    public List<Freelancer> getAllFreelancers() {
        return freelancerRepository.findAll();
    }

    // Obter todos os Employers
    public List<Employer> getAllEmployers() {
        return employerRepository.findAll();
    }

    // Atualizar Freelancer
    public Freelancer updateFreelancer(String cpf, Freelancer updatedFreelancer) {
        Freelancer freelancer = freelancerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found with CPF: " + cpf));

        validateUpdatedUser(cpf, updatedFreelancer.getEmail(), updatedFreelancer.getUsername());

        freelancer.setName(updatedFreelancer.getName());
        freelancer.setUsername(updatedFreelancer.getUsername());
        freelancer.setEmail(updatedFreelancer.getEmail());
        freelancer.setPortfolioLink(updatedFreelancer.getPortfolioLink());

        return freelancerRepository.save(freelancer);
    }

    // Atualizar Employer
    public Employer updateEmployer(String cpf, Employer updatedEmployer) {
        Employer employer = employerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Employer not found with CPF: " + cpf));

        validateUpdatedUser(cpf, updatedEmployer.getEmail(), updatedEmployer.getUsername());

        employer.setName(updatedEmployer.getName());
        employer.setUsername(updatedEmployer.getUsername());
        employer.setEmail(updatedEmployer.getEmail());
        employer.setCompanyName(updatedEmployer.getCompanyName());

        return employerRepository.save(employer);
    }

    // Validações para atualização
    private void validateUpdatedUser(String cpf, String email, String username) {
        if ((freelancerRepository.existsByUsername(username) || employerRepository.existsByUsername(username)) &&
                !freelancerRepository.existsById(cpf) && !employerRepository.existsById(cpf)) {
            throw new RuntimeException("Username is already in use.");
        }
        if ((freelancerRepository.existsByEmail(email) || employerRepository.existsByEmail(email)) &&
                !freelancerRepository.existsById(cpf) && !employerRepository.existsById(cpf)) {
            throw new RuntimeException("Email is already in use.");
        }
    }

    // Deletar Freelancer
    public void deleteFreelancer(String cpf) {
        Freelancer freelancer = freelancerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found with CPF: " + cpf));
        freelancerRepository.delete(freelancer);
    }

    // Deletar Employer
    public void deleteEmployer(String cpf) {
        Employer employer = employerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Employer not found with CPF: " + cpf));
        employerRepository.delete(employer);
    }

    // Buscar Freelancer por Username
    public Freelancer findFreelancerByUsername(String username) {
        return freelancerRepository.findByUsernameContainingIgnoreCase(username).stream().findFirst().orElse(null);
    }

    // Buscar Employer por Username
    public Employer findEmployerByUsername(String username) {
        return employerRepository.findByUsernameContainingIgnoreCase(username).stream().findFirst().orElse(null);
    }

    // Buscar qualquer usuário por Username
    public AbstractUser findUserByUsername(String username) {
        Freelancer freelancer = freelancerRepository.findByUsernameContainingIgnoreCase(username).stream().findFirst().orElse(null);
        if (freelancer != null) {
            return freelancer;
        }
        return employerRepository.findByUsernameContainingIgnoreCase(username).stream().findFirst().orElse(null);
    }
}
