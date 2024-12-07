package com.markcollab.service;

import com.markcollab.dto.FreelancerDTO;
import com.markcollab.model.Freelancer;
import com.markcollab.repository.FreelancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FreelancerService {

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public FreelancerDTO registerFreelancer(Freelancer freelancer) {
        validateFreelancer(freelancer);
        validatePassword(freelancer.getPassword()); // Validação de senha adicionada
        freelancer.setRole("FREELANCER");
        freelancer.setPassword(passwordEncoder.encode(freelancer.getPassword()));
        Freelancer savedFreelancer = freelancerRepository.save(freelancer);

        FreelancerDTO dto = new FreelancerDTO();
        dto.setName(savedFreelancer.getName());
        dto.setUsername(savedFreelancer.getUsername());
        dto.setEmail(savedFreelancer.getEmail());
        dto.setPortfolioLink(savedFreelancer.getPortfolioLink());
        return dto;
    }

    public Freelancer updateFreelancer(String cpf, Freelancer updatedFreelancer) {
        Freelancer freelancer = freelancerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
        freelancer.setName(updatedFreelancer.getName());
        freelancer.setUsername(updatedFreelancer.getUsername());
        freelancer.setEmail(updatedFreelancer.getEmail());
        freelancer.setPortfolioLink(updatedFreelancer.getPortfolioLink());
        return freelancerRepository.save(freelancer);
    }

    public void deleteFreelancer(String cpf) {
        Freelancer freelancer = freelancerRepository.findById(cpf)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));
        freelancerRepository.delete(freelancer);
    }

    public List<FreelancerDTO> getAllFreelancers() {
        return freelancerRepository.findAll()
                .stream()
                .map(freelancer -> {
                    FreelancerDTO dto = new FreelancerDTO();
                    dto.setName(freelancer.getName());
                    dto.setUsername(freelancer.getUsername());
                    dto.setEmail(freelancer.getEmail());
                    dto.setPortfolioLink(freelancer.getPortfolioLink());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    private void validateFreelancer(Freelancer freelancer) {
        if (freelancerRepository.existsByUsername(freelancer.getUsername())) {
            throw new RuntimeException("Username already in use");
        }
        if (freelancerRepository.existsByEmail(freelancer.getEmail())) {
            throw new RuntimeException("Email already in use");
        }
        if (freelancerRepository.existsById(freelancer.getCpf())) {
            throw new RuntimeException("CPF already in use");
        }
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new RuntimeException("Password must contain at least one number");
        }
    }
}
