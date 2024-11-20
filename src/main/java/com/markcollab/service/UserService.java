package com.markcollab.service;

import com.markcollab.model.Employer;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.EmployerRepository;
import com.markcollab.repository.FreelancerRepository;
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

    // Criar Freelancer
    public Freelancer registerFreelancer(Freelancer freelancer) {
        return freelancerRepository.save(freelancer);
    }

    // Criar Employer
    public Employer registerEmployer(Employer employer) {
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
    public Freelancer updateFreelancer(String cpf, Freelancer updatedFreelancer) {
        Optional<Freelancer> optionalFreelancer = freelancerRepository.findByCpf(cpf);

        if (optionalFreelancer.isEmpty()) {
            throw new RuntimeException("Freelancer not found with CPF: " + cpf);
        }

        Freelancer freelancer = optionalFreelancer.get();
        freelancer.setName(updatedFreelancer.getName());
        freelancer.setUsername(updatedFreelancer.getUsername());
        freelancer.setEmail(updatedFreelancer.getEmail());
        freelancer.setPortfolioLink(updatedFreelancer.getPortfolioLink());

        return freelancerRepository.save(freelancer);
    }

    // Atualizar Employer
    public Employer updateEmployer(String cpf, Employer updatedEmployer) {
        Optional<Employer> optionalEmployer = employerRepository.findByCpf(cpf);

        if (optionalEmployer.isEmpty()) {
            throw new RuntimeException("Employer not found with CPF: " + cpf);
        }

        Employer employer = optionalEmployer.get();
        employer.setName(updatedEmployer.getName());
        employer.setUsername(updatedEmployer.getUsername());
        employer.setEmail(updatedEmployer.getEmail());
        employer.setCompanyName(updatedEmployer.getCompanyName());

        return employerRepository.save(employer);
    }

    // Deletar Freelancer
    public void deleteFreelancer(String cpf) {
        Optional<Freelancer> optionalFreelancer = freelancerRepository.findByCpf(cpf);

        if (optionalFreelancer.isEmpty()) {
            throw new RuntimeException("Freelancer not found with CPF: " + cpf);
        }

        freelancerRepository.delete(optionalFreelancer.get());
    }

    // Deletar Employer
    public void deleteEmployer(String cpf) {
        Optional<Employer> optionalEmployer = employerRepository.findByCpf(cpf);

        if (optionalEmployer.isEmpty()) {
            throw new RuntimeException("Employer not found with CPF: " + cpf);
        }

        employerRepository.delete(optionalEmployer.get());
    }
}
